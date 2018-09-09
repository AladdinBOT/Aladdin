/*
 * Developed by HeyZeer0 on 09/09/18 10:23.
 * Last Modification 09/09/18 10:17.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.music.listeners.AudioResultHandler;
import org.apache.http.client.utils.URIBuilder;

import java.util.HashMap;

public class MusicManager {

    JdaLavalink lavaLink;
    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    HashMap<String, GuildController> guildControllers = new HashMap<>();

    public MusicManager(int total_shards, String bot_id) {
        lavaLink = new JdaLavalink(bot_id, total_shards, shard -> Main.getShard(shard).getJDA());

        //registering nodes
        try{
            String nodes = ApiKeysConfig.lavalink_nodes;
            if(nodes.contains(",")) {
                for(String node : nodes.split(",")) {
                    if(node.startsWith(" ")) node = node.substring(1);
                    String[] spplited = node.split("::");
                    lavaLink.addNode(new URIBuilder().setHost(spplited[0]).setPort(Integer.valueOf(spplited[1])).build(), spplited[2]);
                }
            }else{
                String[] spplited = nodes.split("::");
                lavaLink.addNode(new URIBuilder().setHost(spplited[0]).setPort(Integer.valueOf(spplited[1])).build(), spplited[2]);
            }
        }catch (Exception ex) { ex.printStackTrace(); }

        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.MEDIUM);

        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
    }

    public JdaLavalink getLavaLink() {
        return lavaLink;
    }

    public GuildController getGuildController(Guild g) {
        if(!guildControllers.containsKey(g.getId())) guildControllers.put(g.getId(), new GuildController(g, lavaLink.getLink(g)));

        return guildControllers.get(g.getId());
    }

    public void addToQueue(User u, Message msg, String search) {
        playerManager.loadItem(search, new AudioResultHandler(u, msg, search));
    }

    public int runningControllers() {
        return (int) guildControllers.values().stream().filter(GuildController::isRunning).count();
    }

    public boolean isConnected(Guild g) {
        return guildControllers.containsKey(g.getId()) && getGuildController(g).isRunning();
    }


}
