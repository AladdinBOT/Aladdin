package net.heyzeer0.aladdin.music.profiles;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.profiles.ShardProfile;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayerContext {

    private AudioTrack track;
    private long dj, channel;
    private ShardProfile shard;

    public PlayerContext(AudioTrack track, User dj, TextChannel channel, ShardProfile shard) {
        this.track = track;
        this.dj = dj.getIdLong();
        this.channel = channel.getIdLong();
        this.shard = shard;
    }

    public PlayerContext makeClone() {
        track = track.makeClone();
        return this;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public ShardProfile getShard() {
        return shard;
    }

    public User getDJ() {
        return getShard().getJDA().getUserById(dj);
    }

    public TextChannel getChannel() {
        return getShard().getJDA().getTextChannelById(channel);
    }

}
