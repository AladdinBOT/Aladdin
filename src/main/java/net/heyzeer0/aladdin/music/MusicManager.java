package net.heyzeer0.aladdin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.core.entities.Guild;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;
import org.apache.http.client.utils.URIBuilder;

import java.util.function.LongFunction;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MusicManager {

    JdaLavalink lavalink;

    private TLongObjectMap<GuildTrackProfile> musicManagers = new TLongObjectHashMap<>();
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public MusicManager(int shard_amount, String bot_id) {
        lavalink = new JdaLavalink(bot_id, shard_amount, shard -> Main.getShard(shard).getJDA());

        try{
            String nodes = ApiKeysConfig.lavalink_nodes;

            if(nodes.contains(",")) {
                for(String node : nodes.split(",")) {
                    if(node.startsWith(" ")) node = node.substring(1);

                    String[] spplited = node.split("::");
                    Main.getLogger().warn("|" + spplited[0] + "|" + spplited[1] + "|" + spplited[2] + "|");
                    lavalink.addNode(new URIBuilder().setHost(spplited[0]).setPort(Integer.valueOf(spplited[1])).build(), spplited[2]);
                }
            }else{
                String[] spplited = nodes.split("::");
                lavalink.addNode(new URIBuilder().setHost(spplited[0]).setPort(Integer.valueOf(spplited[1])).build(), spplited[2]);
            }
        }catch (Exception ex) { ex.printStackTrace(); }


        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public TLongObjectMap getManagers() {
        return musicManagers;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public GuildTrackProfile getManager(Guild guild) {
        return computeIfAbsent(musicManagers, guild.getIdLong(), (id) -> new GuildTrackProfile(lavalink.getLink(guild.getId()), guild));
    }

    public JdaLavalink getLavaLink() {
        return lavalink;
    }

    private static <T> T computeIfAbsent(TLongObjectMap<T> map, long key, LongFunction<T> function) {
        if (!map.containsKey(key))
            map.put(key, function.apply(key));
        return map.get(key);
    }

}
