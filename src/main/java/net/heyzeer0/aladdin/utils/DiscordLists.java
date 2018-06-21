package net.heyzeer0.aladdin.utils;

import com.github.natanbc.discordbotsapi.DiscordBotsAPI;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.commands.UpvoteCommand;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.enums.EmojiList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 24/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class DiscordLists {

    public static DiscordBotsAPI discordBots;
    private static boolean upvoteStarted = false;

    public static void updateStatus() {

    }

    public static void checkUpvoted() {
        UpvoteCommand.detection_time = System.currentTimeMillis();
        if(!ApiKeysConfig.discord_bots_key.equalsIgnoreCase("<insert-here>")) {
            List<String> upvoters = new ArrayList<>();
            long[] ids = discordBots.getUpvoterIds().execute();
            for(long id : ids) {
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
