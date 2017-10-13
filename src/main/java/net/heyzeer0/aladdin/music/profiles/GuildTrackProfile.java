package net.heyzeer0.aladdin.music.profiles;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.music.utils.AudioUtils;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GuildTrackProfile extends AudioEventAdapter {

    public static final ScheduledExecutorService LEAVE_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(5, r->{
        Thread t = new Thread(r, "VoiceLeaveThread");
        t.setDaemon(true);
        return t;
    });

    private final BlockingQueue<PlayerContext> queue;
    private RepeatMode repeatMode;
    private PlayerContext currentTrack;
    private PlayerContext previousTrack;
    private final AudioPlayer audioPlayer;
    private long lastMessageId, guildId;
    private TLongList voteSkips;
    private ScheduledFuture<?> task;


    public GuildTrackProfile(AudioPlayerManager playerManager, Guild guild) {
        this.audioPlayer = playerManager.createPlayer();
        this.audioPlayer.addListener(this);
        this.guildId = guild.getIdLong();
        this.queue = new LinkedBlockingQueue<>();
        this.repeatMode = null;
        this.currentTrack = null;
        this.previousTrack = null;
        this.lastMessageId = 0;
        this.voteSkips = new TLongArrayList();
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public PlayerSendHandler getSendHandler() {
        return new PlayerSendHandler(audioPlayer);
    }

    public int getRequiredVotes() {
        int listeners = (int) getGuild().getAudioManager().getConnectedChannel().getMembers().stream()
                .filter(m -> !m.getUser().isBot()).count();
        return (int) Math.ceil(listeners * .55);
    }

    public BlockingQueue<PlayerContext> getQueue() {
        return queue;
    }

    public void shuffle() {
        List<PlayerContext> tracks = new ArrayList<>();
        queue.drainTo(tracks);
        Collections.shuffle(tracks);
        queue.addAll(tracks);
        tracks.clear();
    }

    public PlayerContext getCurrentTrack() {
        return currentTrack;
    }

    public PlayerContext getPreviousTrack() {
        return previousTrack;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
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
            audioPlayer.startTrack(currentTrack.makeClone().getTrack(), false);
        } else {
            if (currentTrack != null)
                previousTrack = currentTrack;
            currentTrack = queue.poll();
            audioPlayer.startTrack(currentTrack == null ? null : currentTrack.makeClone().getTrack(), false);
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

    public boolean restart(Member member) {
        if (currentTrack != null && currentTrack.getTrack().getState() == AudioTrackState.PLAYING) {
            currentTrack.getTrack().setPosition(0);
            return true;
        } else if (previousTrack != null && previousTrack.getChannel() != null && AudioUtils.connectChannel(previousTrack.getChannel(), member)) {
            queue.offer(previousTrack.makeClone());
            startNext(true);
            return true;
        }
        return false;
    }

    public boolean scheduleLeave() {
        if (task != null) {
            return false;
        }
        getAudioPlayer().setPaused(true);
        task = LEAVE_EXECUTOR_SERVICE.schedule(() -> {
            TextChannel tc = getCurrentTrack().getChannel();
            stop();
        }, 1, TimeUnit.MINUTES);
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
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
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
        Utils.runAsync(() -> getGuild().getAudioManager().closeAudioConnection());
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        getAudioPlayer().setPaused(false);
        setRepeatMode(null);
        TextChannel tc = previousTrack.getChannel();
        if (tc != null && tc.canTalk())
            tc.sendMessage(":musical_note: Playlist finalizada, desconectando...").queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
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
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
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
