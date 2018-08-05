package net.heyzeer0.aladdin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;

import java.util.function.LongFunction;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MusicManager {

    private static  TLongObjectMap<GuildTrackProfile> musicManagers = new TLongObjectHashMap<>();
    private static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    static {
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static TLongObjectMap<GuildTrackProfile> getManagers() {
        return musicManagers;
    }

    public static AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public static GuildTrackProfile getManager(Guild guild) {
        GuildTrackProfile musicManager = computeIfAbsent(musicManagers, guild.getIdLong(), (id) -> new GuildTrackProfile(playerManager, guild));
        if (guild.getAudioManager().getSendingHandler() == null)
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    private static <T> T computeIfAbsent(TLongObjectMap<T> map, long key, LongFunction<T> function) {
        if (!map.containsKey(key))
            map.put(key, function.apply(key));
        return map.get(key);
    }

}
