package net.heyzeer0.aladdin.manager.custom.osu;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuBeatmapProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuPlayerProfile;
import net.heyzeer0.aladdin.profiles.utilities.ScheduledExecutor;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 21/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuSubscriptionManager {

    private static HashMap<String, ArrayList<String>> subscription;
    private static ArrayList<String> sended_ids = new ArrayList<>();

    private static final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    public static boolean addSubscriptor(User user, String target) {
        if(subscription.containsKey(target) && subscription.get(target).contains(user.getId())) {
            subscription.get(target).remove(user.getId());
            Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
            return false;
        }

        user.openPrivateChannel().queue(pc -> {
            pc.sendMessage(":white_check_mark: Você agora recebera noticias sobre o jogador ``" + target + "``").queue(c -> {
                if(subscription.containsKey(target)) {
                    subscription.get(target).add(user.getId());
                    Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                }else{
                    try{
                        OsuManager.getTop50FromPlayer(target).forEach(k -> sended_ids.add(k.toString()));
                        ArrayList<String> ss = new ArrayList<>(); ss.add(user.getId());
                        subscription.put(target, ss);
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }catch (Exception ex) { }
                }
                });
        });

        return true;
    }

    public static void startUpdating() {
        ThreadManager.registerScheduledExecutor(new ScheduledExecutor(60000, () -> {
            if(subscription == null) {
                subscription = Main.getDatabase().getServer().getOsu_subscriptions();

                if(subscription.size() > 0) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    for(String user : subscription.keySet()) {
                        try{
                            for(OsuMatchProfile c : OsuManager.getTop50FromPlayer(user)) {
                                sended_ids.add(c.toString());
                            }
                        }catch (Exception ex) { ex.printStackTrace(); toRemove.add(user); }
                    }

                    if(toRemove.size() > 0) {
                        toRemove.forEach(c -> subscription.remove(c));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                }
            }else {
                ArrayList<String> toRemove = new ArrayList<>();
                HashMap<String, ArrayList<String>> removeUsers = new HashMap<>();
                sended_ids.remove(0);
                if (subscription.size() > 0) {
                    for (String user : subscription.keySet()) {
                        try {

                            ArrayList<OsuMatchProfile> ls = OsuManager.getTop50FromPlayer(user);
                            for (int i = 0; i < ls.size(); i++) {
                                OsuMatchProfile mp = ls.get(i);
                                if (!sended_ids.contains(mp.toString())) {
                                    sended_ids.add(mp.toString());

                                    OsuPlayerProfile pp = OsuManager.getUserProfile(mp.getUser_id(), true);
                                    OsuBeatmapProfile bp = OsuManager.getBeatmap(mp.getBeatmap_id());

                                    EmbedBuilder eb = new EmbedBuilder();
                                    eb.setColor(Color.GREEN);
                                    eb.setImage("https://assets.ppy.sh/beatmaps/" + bp.getBeatmap_id() + "/covers/cover.jpg");
                                    eb.setThumbnail("https://a.ppy.sh/" + mp.getUser_id());
                                    eb.setTitle("Novo Rank #" + (i + 1) + " para " + pp.getNome());
                                    eb.setDescription("Clique [aqui](https://osu.ppy.sh/users/" + mp.getUser_id() + ") para ir ao perfil do jogador.");
                                    eb.addField(":trophy: | Status:", "**pp:** " + mp.getPp(), true);
                                    double percentage = calculatePercentage(Integer.valueOf(mp.getCount50()), Integer.valueOf(mp.getCount100()), Integer.valueOf(mp.getCount300()), Integer.valueOf(mp.getCountmiss())) * 100;

                                    int count50 = Integer.valueOf(mp.getCount50());
                                    int count100 = Integer.valueOf(mp.getCount100());
                                    int count300 = Integer.valueOf(mp.getCount300());
                                    int countMiss = Integer.valueOf(mp.getCountmiss());

                                    Main.getLogger().warn((((count50 * 50) + (count100 * 100) + (count300 * 300)) / ((countMiss + count50 + count100 + count300) * 300) * 100) + "% " + count50 + " " + count100 + " " + count300 + " " + countMiss);

                                    Main.getLogger().warn(percentage + "% " + mp.getCount50() + " " + mp.getCount100() + " " + mp.getCount300() + " " + mp.getCountmiss());

                                    eb.addField("<:empty:363753754874478602>", "**Rank:** " + mp.getRank().replace("H", "+") + " | " + decimalFormat.format(percentage) + "%", true);


                                    for (String usr : subscription.get(user)) {
                                        User u = Main.getUserById(usr);
                                        eb.setFooter("Status requerido por " + u.getName(), u.getEffectiveAvatarUrl());

                                        u.openPrivateChannel().queue(c -> c.sendMessage(eb.build()).queue(v -> {
                                        }, k -> {
                                            if (removeUsers.containsKey(user)) {
                                                removeUsers.get(user).add(usr);
                                            } else {
                                                ArrayList<String> rmv = new ArrayList<>();
                                                rmv.add(usr);
                                                removeUsers.put(user, rmv);
                                            }
                                        }), er -> {
                                            if (removeUsers.containsKey(user)) {
                                                removeUsers.get(user).add(usr);
                                            } else {
                                                ArrayList<String> rmv = new ArrayList<>();
                                                rmv.add(usr);
                                                removeUsers.put(user, rmv);
                                            }
                                        });
                                    }

                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            toRemove.add(user);
                        }
                    }

                    if (toRemove.size() > 0) {
                        toRemove.forEach(c -> subscription.remove(c));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                    if (removeUsers.size() > 0) {
                        removeUsers.keySet().forEach(c -> subscription.get(c).removeAll(removeUsers.get(c)));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                }
            }
        }));
    }

    private static double calculatePercentage(int count50, int count100, int count300, int misses) {
        return ((count50 * 50) + (count100 * 100) + (count300 * 300)) / ((misses + count50 + count100 + count300) * 300);
    }

}
