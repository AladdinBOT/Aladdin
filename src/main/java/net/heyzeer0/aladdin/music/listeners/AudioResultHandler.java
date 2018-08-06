package net.heyzeer0.aladdin.music.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.music.instances.MusicContext;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.utilities.chooser.Chooser;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 05/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class AudioResultHandler implements AudioLoadResultHandler {

    Message m; User u; String search; LangProfile lp;

    final int MAX_SONG_LENGTH = 1260000; final int MAX_PLAYLIST_SIZE = 60;

    public AudioResultHandler(User u, Message m, String search) {
        this.m = m; this.u = u; this.search = search;
        this.lp = Main.getDatabase().getGuildProfile(m.getGuild()).getSelectedLanguage().getLangProfile();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if(track.getDuration() > MAX_SONG_LENGTH && !Main.getDatabase().getUserProfile(u).userPremium()) {
            m.editMessage(lp.get("music.durationexceed", Main.getDatabase().getGuildProfile(m.getGuild()).getConfigValue(GuildConfig.PREFIX) + "premium features")).queue();
            return;
        }

        GuildController controller = Main.getMusicManager().getGuildController(m.getGuild());
        controller.addToQueue(new MusicContext(track, u.getId(), m.getTextChannel().getId(), Main.getShardForGuild(m.getGuild().getIdLong()).getShardId()));
        m.delete().queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if(playlist.isSearchResult()) {
            Chooser ch = new Chooser(m, lp.get("music.selector.title"));
            AudioTrack[] options = playlist.getTracks().stream().limit(3).toArray(AudioTrack[]::new);
            for (AudioTrack track : options) {
                ch.addOption(lp.get("music.selector.option", track.getInfo().title, Utils.format(track.getDuration())), () -> trackLoaded(track));
            }

            ch.start();
            return;
        }

        if(playlist.getTracks().size() > MAX_PLAYLIST_SIZE&& !Main.getDatabase().getUserProfile(u).userPremium()) {
            m.editMessage(lp.get("music.playlistlimitexceed", MAX_PLAYLIST_SIZE, Main.getDatabase().getGuildProfile(m.getGuild()).getConfigValue(GuildConfig.PREFIX) + "premium features")).queue();
            return;
        }

        GuildController controller = Main.getMusicManager().getGuildController(m.getGuild());
        playlist.getTracks().stream().filter(c -> Main.getDatabase().getUserProfile(u).userPremium() || c.getDuration() <= MAX_SONG_LENGTH).forEach(c -> controller.addToQueue(new MusicContext(c, u.getId(), m.getTextChannel().getId(), Main.getShardForGuild(m.getGuild().getIdLong()).getShardId())));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        m.editMessage(lp.get("music.loadfailed", exception.getMessage())).queue(c -> c.delete().queueAfter(30, TimeUnit.SECONDS));;
    }

    @Override
    public void noMatches() {
        if(!search.startsWith("ytsearch:")) {
            Main.getMusicManager().addToQueue(u, m, "ytsearch:" + search);
            return;
        }

        m.editMessage(lp.get("music.nomatches", (search.startsWith("ytsearch:") ? search.substring(9) : search).trim())).queue(c -> c.delete().queueAfter(30, TimeUnit.SECONDS));
    }

}
