package net.heyzeer0.aladdin.music.instances;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.manager.custom.VagalumeManager;
import net.heyzeer0.aladdin.utils.Utils;

/**
 * Created by HeyZeer0 on 05/08/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class MusicContext {

    private AudioTrack audioTrack;
    private String dj, channel;
    private int shard_id;

    private String lyrics;

    public MusicContext(AudioTrack audioTrack, String dj, String channel, int shard_id) {
        this.audioTrack = audioTrack; this.dj = dj; this.channel = channel; this.shard_id = shard_id;

        if(audioTrack.getInfo().title.contains(" - ")) {
            String[] spplited = audioTrack.getInfo().title.split(" - ");
            Utils.runAsync(() -> { try{ lyrics = VagalumeManager.getLyrics(spplited[0], spplited[1]).replace("\\n", "\n"); }catch (Exception ignored) {} });
        }
    }

    public User getDJ() {
        return Main.getShard(shard_id).getJDA().getUserById(dj);
    }

    public TextChannel getChannel() {
        return Main.getShard(shard_id).getJDA().getTextChannelById(channel);
    }

    public MusicContext clone() {
        return new MusicContext(audioTrack.makeClone(), dj, channel, shard_id);
    }

    public Member getDJasMember() {
        return getChannel().getGuild().getMemberById(getDJ().getId());
    }

    public String getLyrics() {
        return lyrics;
    }

}
