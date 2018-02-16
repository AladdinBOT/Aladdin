package net.heyzeer0.aladdin.manager.custom.warframe;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.profiles.custom.warframe.SubscriptionProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONObject;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 16/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SubscriptionManager {

    public static HashMap<String, SubscriptionProfile> subscriptions = null;
    public static boolean acceptNight = true;

    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public static void addSubscriptor(User u) {
        u.openPrivateChannel().queue(sc -> sc.sendMessage(":white_check_mark: Você agora recebera noticias sobre o jogo").queue(scc -> {
            subscriptions.put(u.getId(), new SubscriptionProfile());
            Main.getDatabase().getServer().updateSubscriptions(subscriptions);
        }, fl -> {}), fl -> {});
    }

    public static void removeSubscriptor(User u) {
        subscriptions.remove(u.getId());

        Main.getDatabase().getServer().updateSubscriptions(subscriptions);
    }

    public static void startUpdating() {
        timer.scheduleAtFixedRate(() -> {
            if(subscriptions == null) {
                subscriptions = Main.getDatabase().getServer().getSubscriptions();
            }else{

                boolean sendNight = false;
                int amount = subscriptions.size();

                try{
                    JSONObject cycle = new JSONObject(Utils.readWebsite("https://api.warframestat.us/pc/cetusCycle"));

                    if(!cycle.getBoolean("isDay")) {
                        if(acceptNight) {
                            acceptNight = false;
                            sendNight = true;
                        }
                    }else{
                        acceptNight = true;
                    }

                    for(String userId : subscriptions.keySet()) {
                        User u = Main.getUserById(userId);
                        List<Guild> mutual = Main.getMutualGuilds(u);

                        if(mutual.size() <= 0) {
                            subscriptions.remove(userId);
                        }else{
                            Member m = mutual.get(0).getMember(u);

                            if(m.getOnlineStatus() == OnlineStatus.ONLINE) {

                                if(sendNight || u.getId().equalsIgnoreCase("169904764048375809")) {
                                    EmbedBuilder b = new EmbedBuilder();
                                    b.setTitle("<:level:363725048881610753> Status das planícies");
                                    b.setDescription("É noite nas planícies!\nTempo restante: ``" + cycle.getString("timeLeft") + "``");
                                    b.setFooter("Requerido por " + u.getName(), u.getEffectiveAvatarUrl());
                                    b.setTimestamp(LocalDateTime.now());
                                    b.setColor(Color.GREEN);

                                    u.openPrivateChannel().queue((success) -> success.sendMessage(b.build()).queue(), (failure) -> subscriptions.remove(userId));
                                }
                            }
                        }
                    }
                }catch (Exception ex) { ex.printStackTrace();}

                if(amount != subscriptions.size())
                    Main.getDatabase().getServer().updateSubscriptions(subscriptions);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

}
