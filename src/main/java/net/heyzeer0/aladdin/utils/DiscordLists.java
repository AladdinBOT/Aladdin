package net.heyzeer0.aladdin.utils;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import com.github.natanbc.discordbotsapi.PostingException;
import com.github.natanbc.discordbotsapi.UpvoterInfo;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.profiles.ShardProfile;

import java.util.stream.Stream;

/**
 * Created by HeyZeer0 on 24/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class DiscordLists {

    public static DiscordBotsAPI discordBots;

    public static void updateStatus() {
        if(!ApiKeysConfig.discord_bots_key.equalsIgnoreCase("<insert-here>")) {
            discordBots = new DiscordBotsAPI(ApiKeysConfig.discord_bots_key);
        }

        Utils.runAsync(() -> {
            ShardProfile[] shards = Main.getShards();
            int[] payload = new int[shards.length];
            for(int i = 0; i < shards.length; i++) {
                payload[i] = shards[i].getJDA().getGuilds().size();
            }
            try {
                discordBots.postStats(shards[0].getJDA().getSelfUser().getIdLong(), payload);
            } catch(PostingException e) {
                Main.getLogger().warn("An error ocurred while trying to post status to discordbots.org");
                e.printStackTrace();
            }
        });
    }

}
