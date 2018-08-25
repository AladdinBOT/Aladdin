package net.heyzeer0.aladdin.utils;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.profiles.ShardProfile;
import org.discordbots.api.client.DiscordBotListAPI;

/**
 * Created by HeyZeer0 on 24/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class DiscordLists {

    public static DiscordBotListAPI discordBots;
    private static boolean upvoteStarted = false;

    private static void registerLists() {
        discordBots = new DiscordBotListAPI.Builder().token(ApiKeysConfig.discord_bots_key).botId(BotConfig.bot_id).build();
    }

    public static void updateStatus() {
        registerLists();

        if(discordBots != null) {
            for(ShardProfile c : Main.getShards()) {
                discordBots.setStats(c.getShardId(), Main.getShards().length, c.getJDA().getGuilds().size());
            }
        }
    }

}
