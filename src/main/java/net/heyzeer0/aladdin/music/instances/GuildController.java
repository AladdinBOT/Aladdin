package net.heyzeer0.aladdin.music.instances;

import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.Lang;
import net.heyzeer0.aladdin.music.enums.RepeatMode;
import net.heyzeer0.aladdin.music.listeners.PlayerHandler;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 05/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class GuildController {

    String guild_id;
    int shard_id;
    JdaLink jdaLink;

    BlockingQueue<MusicContext> queue = new LinkedBlockingQueue<>();
    boolean running = false;
    IPlayer player;

    RepeatMode repeatMode = RepeatMode.OFF;
    MusicContext currentTrack;
    MusicContext lastTrack;
    String lastMessageId;
    ArrayList<String> vote_skips = new ArrayList<>();
    String channel_name = "";

    Lang lang;

    public GuildController(Guild g, JdaLink jdaLink) {
        this.guild_id = g.getId(); this.shard_id = Main.getShardForGuild(g.getIdLong()).getShardId(); this.jdaLink = jdaLink;

        player = jdaLink.getPlayer();
        player.addListener(new PlayerHandler(this));
        lang = Main.getDatabase().getGuildProfile(g).getSelectedLanguage();
    }

    public void changeRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public MusicContext getCurrentTrack() {
        return currentTrack;
    }

    public LangProfile getLangProfile() {
        return lang.getLangProfile();
    }

    public Guild getGuild() {
        return Main.getGuildById(guild_id);
    }

    public String getChannelName() {
        return channel_name;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<MusicContext> getQueue() {
        return queue;
    }

    public boolean isRunning() {
        return running;
    }

    public void addToQueue(MusicContext context) {
        if (attemptToJoinChannel(context.getChannel(), context.getDJasMember())) {
            queue.offer(context);

            if(currentTrack == null) currentTrack = context;

            sendMessage(lang.getLangProfile().get("music.addtoqueue", context.getDJasMember().getEffectiveName(), context.getAudioTrack().getInfo().title, Utils.format(context.getAudioTrack().getDuration()), queue.size()));

            if (player.getPlayingTrack() == null) {
                startNext(false);
                running = true;
            }
        }
    }

    public void deleteAndCreateMessage(String text) {
        if(lastMessageId != null && getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY)) {
            Message msg = currentTrack.getChannel().getMessageById(lastMessageId).complete();
            if(msg != null) msg.delete().queue();
        }

        lastMessageId = currentTrack.getChannel().sendMessage(text).complete().getId();
    }

    public void sendMessage(String text) {
        currentTrack.getChannel().sendMessage(text).queue(c -> c.delete().queueAfter(30, TimeUnit.SECONDS));
    }

    //logic
    public void computeSkipVote(Member u) {
        if(vote_skips.contains(u.getUser().getId())) {
            vote_skips.remove(u.getUser().getId());

            sendMessage(getLangProfile().get("music.skipremoved", u.getUser().getName(), vote_skips.size(), getRequiredVotes(u)));
            return;
        }

        vote_skips.add(u.getUser().getId());

        if(getRequiredVotes(u) <= vote_skips.size()) {
            startNext(true);

            sendMessage(getLangProfile().get("music.skipped"));
        }else{
            sendMessage(getLangProfile().get("music.skipadded", u.getUser().getName(), vote_skips.size(), getRequiredVotes(u)));
        }
    }

    public void startNext(boolean skipped) {
        vote_skips.clear();

        if(repeatMode == RepeatMode.SONG && !skipped && currentTrack != null) {
            player.playTrack(currentTrack.getAudioTrack().makeClone());
            return;
        }

        if(queue.size() <= 0) {
            queueFinish();
            return;
        }

        if(currentTrack != null) lastTrack = currentTrack.clone();

        currentTrack = queue.poll();
        player.playTrack(currentTrack.getAudioTrack());

        if(repeatMode == RepeatMode.QUEUE && !skipped && lastTrack != null) queue.offer(lastTrack.clone());
    }

    public int queueFinish() {
        deleteAndCreateMessage(lang.getLangProfile().get("music.queuefinished"));

        repeatMode = RepeatMode.OFF;
        int qsize = queue.size();
        currentTrack = null;
        vote_skips.clear();
        jdaLink.disconnect();
        queue.clear();
        running = false;
        channel_name = null;

        if(player.getPlayingTrack() != null) player.stopTrack();

        return qsize+=1;
    }

    private boolean attemptToJoinChannel(TextChannel tc, Member member) {
        if (tc.getGuild().getAudioManager().isConnected() || tc.getGuild().getAudioManager().isAttemptingToConnect()) {
            while (!tc.getGuild().getAudioManager().isConnected()) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
            }
            if (!tc.getGuild().getAudioManager().getConnectedChannel().equals(member.getVoiceState().getChannel())) {
                tc.sendMessage(lang.getLangProfile().get("music.notonsamechannel")).queue();
                return false;
            }
            return true;
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            tc.sendMessage(lang.getLangProfile().get("music.notonchannel")).queue();
            return false;
        }


        VoiceChannel vc = member.getVoiceState().getChannel();
        if (!tc.getGuild().getSelfMember().hasPermission(vc, Permission.VOICE_CONNECT)) {
            tc.sendMessage(lang.getLangProfile().get("music.nopermission")).queue();
            return false;
        } else if (vc.getUserLimit() > 0 && vc.getMembers().size() > vc.getUserLimit() && !tc.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            tc.sendMessage(lang.getLangProfile().get("music.roomlimit")).queue();
            return false;
        }

        channel_name = vc.getName();
        jdaLink.connect(vc);
        return true;
    }

    public int getRequiredVotes(Member u) {
        int listeners = (int) u.getVoiceState().getChannel().getMembers().stream().filter(m -> !m.getUser().isBot()).count();
        return (int) Math.ceil(listeners * .55);
    }

}
