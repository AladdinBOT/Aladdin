package net.heyzeer0.aladdin.music.profiles;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.music.utils.AudioUtils;
import net.heyzeer0.aladdin.profiles.utilities.chooser.Chooser;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;

import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AudioLoaderProfile implements AudioLoadResultHandler {

    public static final long MAX_SONG_LENGTH = 1260000, MAX_PLAYLIST_LENGTH = 120;

    private boolean force;
    private Message message;
    private String search;
    private User user;
    private String playlist;
    private boolean send_msgs;

    private AudioLoaderProfile(User user, Message message, String search, boolean force, String playlist, boolean send_msgs) {
        this.user = user;
        this.message = message;
        this.search = search;
        this.force = force;
        this.playlist = playlist;
        this.send_msgs = true;
    }

    public static void loadAndPlay(User user, Message msg, String search, boolean force) {
        MusicManager.getPlayerManager().loadItem(search, new AudioLoaderProfile(user, msg, search, force, null, true));
    }

    public static void loadAndPlay(User user, Message msg, String search, boolean force, boolean send_msgs) {
        MusicManager.getPlayerManager().loadItem(search, new AudioLoaderProfile(user, msg, search, force, null, send_msgs));
    }

    public static void addToPlaylist(User user, Message msg, String search, boolean force, String playlist) {
        MusicManager.getPlayerManager().loadItem(search, new AudioLoaderProfile(user, msg, search, force, playlist, true));
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (track.getDuration() > MAX_SONG_LENGTH && !Main.getDatabase().getUserProfile(user).userPremium()) {
            message.getChannel().sendMessage(EmojiList.WORRIED + " Você não pode adicionar musicas que ultrapassam 21 minutos!").queue();
            return;
        }
        Member member = message.getGuild().getMember(user);
        if (AudioUtils.connectChannel(message.getTextChannel(), member)) {
            GuildTrackProfile scheduler = MusicManager.getManager(message.getGuild());
            scheduler.offer(new PlayerContext(track, user, message.getTextChannel(), Main.getShard(user.getJDA().getShardInfo() == null ? 0 : user.getJDA().getShardInfo().getShardId())));

            if(send_msgs)
                message.editMessage(":musical_note: " + user.getName() + " adicionou a musica `" + track.getInfo().title + "` na playlist! (`" + AudioUtils.format(track.getDuration()) + "`) [`" + scheduler.getQueue().size() + "`]").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            else {
                message.delete().queueAfter(30, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist pl) {
        if (pl.isSearchResult()) {
            if (force) {
                trackLoaded(pl.getTracks().get(0));
                return;
            }

            Chooser ch = new Chooser(message, ":musical_note: Selecione o resultado:");
            AudioTrack[] options = pl.getTracks().stream().limit(3).toArray(AudioTrack[]::new);
            for (AudioTrack track : options) {
                ch.addOption(track.getInfo().title + " ``(" + AudioUtils.format(track.getDuration()) + ")``", () -> {
                    if(playlist == null) {
                        trackLoaded(track);
                    }else{

                        message.editMessage(EmojiList.CORRECT + " Você adicionou a música ``" + track.getInfo().title + "`` com sucesso a playlist ``" + playlist + "``").queue();

                        Main.getDatabase().getUserProfile(user).addTrackToPlaylist(playlist, track.getInfo().title, AudioUtils.format(track.getInfo().length), track.getInfo().uri);

                    }
                });
            }

            ch.start();
            return;
        }

        if (pl.getTracks().size() > MAX_SONG_LENGTH  && !Main.getDatabase().getUserProfile(user).userPremium()) {
            message.editMessage(EmojiList.WORRIED + " Você não pode adicionar playlists que possuam mais de  " + MAX_PLAYLIST_LENGTH + " musicas!").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        if (AudioUtils.connectChannel(message.getTextChannel(), message.getGuild().getMember(user))) {
            GuildTrackProfile scheduler = MusicManager.getManager(message.getGuild());
            pl.getTracks().forEach(audioTrack -> scheduler.offer(new PlayerContext(audioTrack, user, message.getTextChannel(), Main.getShard(user.getJDA().getShardInfo() == null ? 0 : user.getJDA().getShardInfo().getShardId()))));
        }
    }

    @Override
    public void noMatches() {
        if (!search.startsWith("ytsearch:")) {
            loadAndPlay(user, message, "ytsearch:" + search, force);
            return;
        }
        message.editMessage(EmojiList.WORRIED + " Parece que eu não encontrei nada sobre ``" + (search.startsWith("ytsearch:") ? search.substring(9) : search).trim() + "``.").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
    }

    @Override
    public void loadFailed(FriendlyException ex) {
        message.editMessage(EmojiList.WORRIED + " Oops ocorreu um erro ao carregar a musica ``" + ex.getMessage() + "``").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
    }

}
