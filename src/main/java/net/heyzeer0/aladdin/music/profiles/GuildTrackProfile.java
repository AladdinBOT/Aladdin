package net.heyzeer0.aladdin.music.profiles;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.music.utils.AudioUtils;

import java.util.concurrent.*;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GuildTrackProfile extends PlayerEventListenerAdapter {

    public static final ScheduledExecutorService LEAVE_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(5, r->{
        Thread t = new Thread(r, "VoiceLeaveThread");
        t.setDaemon(true);
        return t;
    });

    private final BlockingQueue<PlayerContext> queue;
    private RepeatMode repeatMode;
    private PlayerContext currentTrack;
    private PlayerContext previousTrack;
    private final JdaLink link;
    private final LavalinkPlayer audioPlayer;
    private long lastMessageId, guildId;
    private TLongList voteSkips;
    private ScheduledFuture<?> task;


    public GuildTrackProfile(JdaLink link, Guild guild) {
        this.link = link;
        this.audioPlayer = link.getPlayer();
        this.guildId = guild.getIdLong();
        this.queue = new LinkedBlockingQueue<>();
        this.repeatMode = null;
        this.currentTrack = null;
        this.previousTrack = null;
        this.lastMessageId = 0;
        this.voteSkips = new TLongArrayList();

        this.audioPlayer.addListener(this);
    }

    public JdaLink getLink() {
        return link;
    }

    public IPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public int getRequiredVotes() {
        int listeners = (int) getGuild().getAudioManager().getConnectedChannel().getMembers().stream()
                .filter(m -> !m.getUser().isBot()).count();
        return (int) Math.ceil(listeners * .55);
    }

    public BlockingQueue<PlayerContext> getQueue() {
        return queue;
    }

    public PlayerContext getCurrentTrack() {
        return currentTrack;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public Guild getGuild() {
        return Main.getShards()[(int) ((guildId >> 22) % Main.getShards().length)].getJDA().getGuildById(guildId);
    }

    public void setLastMessage(Message message) {
        this.lastMessageId = message.getIdLong();
    }

    public void startNext(boolean isSkipped) {
        voteSkips.clear();
        if (RepeatMode.SONG == repeatMode && !isSkipped && currentTrack != null) {
            audioPlayer.playTrack(currentTrack.makeClone().getTrack());
        } else {
            if (currentTrack != null)
                previousTrack = currentTrack;
            currentTrack = queue.poll();
            audioPlayer.playTrack(currentTrack == null ? null : currentTrack.makeClone().getTrack());
            if (!isSkipped && RepeatMode.QUEUE == repeatMode && previousTrack != null)
                queue.offer(previousTrack.makeClone());
        }
        if (currentTrack == null)
            onQueueEnd();
    }

    public void offer(PlayerContext trackContext) {
        this.queue.offer(trackContext);
        if (audioPlayer.getPlayingTrack() == null)
            startNext(false);
    }

    public TLongList getVoteSkips() {
        return voteSkips;
    }

    public void skip() {
        startNext(true);
    }

    public int stop() {
        int removedSongs = queue.size();
        queue.clear();
        startNext(true);
        return removedSongs;
    }

    public boolean scheduleLeave() {
        if (task != null) {
            return false;
        }
        getAudioPlayer().setPaused(true);
        task = LEAVE_EXECUTOR_SERVICE.schedule((Runnable) this::stop, 1, TimeUnit.MINUTES);
        return true;
    }

    public boolean cancelLeave() {
        if (task != null) {
            task.cancel(true);
            getAudioPlayer().setPaused(false);
            task = null;
            return true;
        }
        return false;
    }

    @Override
    public void onTrackStart(IPlayer player, AudioTrack track) {
        TextChannel channel = currentTrack.getChannel();
        if (channel != null && channel.canTalk()) {
            VoiceChannel vc = getGuild().getAudioManager().isAttemptingToConnect() ? getGuild().getAudioManager().getQueuedAudioConnection() : getGuild().getAudioManager().getConnectedChannel();
            if (vc == null) {
                channel.sendMessage(EmojiList.WORRIED + "Ops, parece que eu perdi conexão com o canal de audio!").queue();
                stop();
                return;
            }
            AudioTrackInfo info = track.getInfo();
            channel.sendMessage(":musical_note: Tocando agora ``" + info.title + "``(" + AudioUtils.format(info.length) + ") em ``" + vc.getName() + "`` adicionada por " + currentTrack.getDJ().getName()).queue(this::setLastMessage);
        }
    }

    private void onQueueEnd() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }

        getAudioPlayer().setPaused(false);
        setRepeatMode(null);
        TextChannel tc = previousTrack.getChannel();
        if (tc != null && tc.canTalk())
            tc.sendMessage(":musical_note: Playlist finalizada, desconectando...").queue();

        link.disconnect();
    }

    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        voteSkips.clear();
        if (currentTrack != null) {
            TextChannel channel = currentTrack.getChannel();
            if (channel != null && channel.canTalk()) {
                channel.deleteMessageById(lastMessageId).queue();
            }
        }
        if (endReason.mayStartNext) {
            startNext(false);
        }
    }

    @Override
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        try {
            TextChannel channel = currentTrack.getChannel();
            if (channel != null && channel.canTalk()) {
                Guild guild = channel.getGuild();
                String msg = EmojiList.WORRIED + " Oops, não foi possível reproduzir a musica ``" + track.getInfo().title + "``: " + exception.getMessage();
                if (guild.getSelfMember().hasPermission(Permission.MESSAGE_HISTORY))
                    channel.getMessageById(lastMessageId).queue(message -> message.editMessage(msg).queue(), throwable -> channel.sendMessage(msg).queue());
                else
                    channel.sendMessage(msg).queue();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        try {
            TextChannel channel = currentTrack.getChannel();
            if (channel != null && channel.canTalk()) {
                Guild guild = channel.getGuild();
                String msg =  EmojiList.WORRIED + " Oops, a musica travou! estou pulando-a";
                if (guild.getSelfMember().hasPermission(Permission.MESSAGE_HISTORY))
                    channel.getMessageById(lastMessageId).queue(message -> message.editMessage(msg).queue(), throwable -> channel.sendMessage(msg).queue());
                else
                    channel.sendMessage(msg).queue();
            }
        } catch (Exception ignored) {}
    }

    public enum RepeatMode {
        SONG, QUEUE
    }

}
