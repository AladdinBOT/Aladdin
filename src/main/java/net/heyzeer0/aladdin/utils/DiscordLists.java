package net.heyzeer0.aladdin.utils;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.profiles.ShardProfile;

public class DiscordLists {

    public static DiscordBotsAPI discordBots;

    public static void updateStatus() {
        if(!ApiKeysConfig.discord_bots_key.equalsIgnoreCase("<insert-here>")) {
            discordBots = new DiscordBotsAPI(ApiKeysConfig.discord_bots_key);

        }

        for(ShardProfile shard : Main.getConnectedShards()) {
            Utils.runAsync(() -> {
                discordBots.postStats(shard.getShardId(), Main.getShards().length, shard.getJDA().getGuilds().size());
            });
        }

    }

}
