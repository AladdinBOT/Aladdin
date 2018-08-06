package net.heyzeer0.aladdin.music.listeners;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.core.entities.TextChannel;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.utils.Utils;

/**
 * Created by HeyZeer0 on 05/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayerHandler extends PlayerEventListenerAdapter {

    GuildController c;

    public PlayerHandler(GuildController c) {
        this.c = c;
    }

    public void onTrackStart(IPlayer player, AudioTrack track) {
        TextChannel channel = c.getCurrentTrack().getChannel();

        if(channel != null && channel.canTalk()) {
            c.deleteAndCreateMessage(c.getLangProfile().get("music.nowplaying", track.getInfo().title, Utils.format(track.getDuration()), c.getChannelName(), c.getCurrentTrack().getDJasMember().getEffectiveName()));
        }
    }


    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext) c.startNext(false);
    }


    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        c.sendMessage(c.getLangProfile().get("music.cannotreproduce", track.getInfo().title, exception.getMessage()));

        c.startNext(true);
    }


    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        c.sendMessage(c.getLangProfile().get("music.stuck"));

        c.startNext(true);
    }

}
