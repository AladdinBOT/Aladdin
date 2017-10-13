package net.heyzeer0.aladdin.utils;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import com.github.natanbc.discordbotsapi.PostingException;
import com.github.natanbc.discordbotsapi.UpvoterInfo;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.commands.UpvoteCommand;
import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.profiles.ShardProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 24/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class DiscordLists {

    public static DiscordBotsAPI discordBots;
    private static boolean upvoteStarted = false;

    public static void updateStatus() {
        if(!ApiKeysConfig.discord_bots_key.equalsIgnoreCase("<insert-here>")) {
            discordBots = new DiscordBotsAPI(ApiKeysConfig.discord_bots_key);

            if(!upvoteStarted) {
                Utils.runTimer(DiscordLists::checkUpvoted, 10, TimeUnit.MINUTES);
                upvoteStarted = true;
            }
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

    public static void checkUpvoted() {
        UpvoteCommand.detection_time = System.currentTimeMillis();
        if(!ApiKeysConfig.discord_bots_key.equalsIgnoreCase("<insert-here>")) {
            List<String> upvoters = new ArrayList<>();
            for(long id : discordBots.getUpvoterIds(Main.getShard(0).getJDA().getSelfUser().getIdLong())) {
                upvoters.add(id + "");
                if(!Main.getDatabase().getServer().isUserUpvoted(id + "")) {
                    Main.getDatabase().getUserProfile(id + "").activateTrialPremium();
                    Main.getDatabase().getServer().addUpvoted(id + "");

                    User u = Main.getUserById(id);
                    if(u != null) {
                        u.openPrivateChannel().queue(pv -> pv.sendMessage(EmojiList.CORRECT + " Seu trial-premium foi ativado com sucesso!").queue());
                    }
                }
            }

            List<String> non_active = new ArrayList<>();

            for(String k : Main.getDatabase().getServer().getUsers_who_upvoted()) {
                if(!upvoters.contains(k)) {
                    non_active.add(k);
                }
            }

            if(non_active.size() > 0) {
                for(String id : non_active) {
                    if(Main.getDatabase().getUserProfile(id).isTrialPremium()) {
                        Main.getDatabase().getUserProfile(id).disablePremium();
                    }
                }
            }
        }
    }

}
