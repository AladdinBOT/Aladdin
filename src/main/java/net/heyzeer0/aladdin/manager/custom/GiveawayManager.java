package net.heyzeer0.aladdin.manager.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.profiles.GiveawayProfile;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.utils.Utils;
import net.heyzeer0.aladdin.utils.builders.GiveawayBuilder;
import net.heyzeer0.aladdin.utils.builders.Prize;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 14/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GiveawayManager {

    public static HashMap<String, GiveawayProfile> giveways = new HashMap<>();
    public static boolean already_requested = false;

    private static ScheduledExecutorService giveTimer = Executors.newSingleThreadScheduledExecutor();

    public static void createGiveway(GiveawayBuilder b) {
        if(!b.getCh().canTalk()) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setTitle(EmojiList.MONEY + " " + b.getName());
        eb.setDescription("Para participar clique na reação :white_check_mark:");
        eb.setFooter("Iniciado por " + b.getE().getAuthor().getName(), b.getE().getAuthor().getEffectiveAvatarUrl());
        String premios = "";

        for(Prize p : b.getPrizes()) {
            premios = premios + " - " + p.getName() + "\n";
        }

        eb.addField(":tada: Premios", premios, false);
        eb.addField(":stopwatch: Tempo restante", Utils.getTime(b.getEnd_time()), false);

        Message msg = b.getCh().sendMessage(eb.build()).complete();

        if(msg != null) {
            msg.addReaction("✅").complete();
            giveways.put(msg.getId(), new GiveawayProfile(msg.getId(), b.getCh().getId(), b.getCh().getGuild().getId(), b.getE().getAuthor().getName(), b.getE().getAuthor().getEffectiveAvatarUrl(), b.getName(), b.getPrizes(), System.currentTimeMillis() + b.getEnd_time()));
            Main.getDatabase().getServer().updateGiveways(giveways);
        }
    }

    public static void startUpdating() {
        giveTimer.scheduleAtFixedRate(() -> {
            if(giveways.size() <= 0) {
                if(!already_requested) {
                    giveways = Main.getDatabase().getServer().getGiveaways();
                    giveways.remove("451827296299384832");
                    giveways.remove("451828123328053258");
                    giveways.remove("451829527136567298");
                    giveways.remove("451949849646989313");
                    giveways.remove("452251098472775681");
                    giveways.remove("452251098472775681");

                    Main.getDatabase().getServer().updateGiveways(giveways);
                    already_requested = true;
                }
            }else{
                ArrayList<String> toCleanup = new ArrayList<>();

                for(String id : giveways.keySet()) {
                    GiveawayProfile g = giveways.get(id);
                    TextChannel ch = Main.getGuildById(g.getGuildID()).getTextChannelById(g.getChannelID());

                    if (ch == null || ch.getMessageById(g.getMessageID()).complete() == null) {
                        toCleanup.add(id);
                    }else{

                        if(g.getEndTime() - System.currentTimeMillis() <= 0) {
                            Message msg = ch.getMessageById(g.getMessageID()).complete();

                            HashMap<User, Prize> winners = new HashMap<>();

                            for(MessageReaction rc : msg.getReactions()) {
                                if(rc.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                                    List<User> usr = rc.getUsers().complete();
                                    Integer count = 0;
                                    if(usr.size() <= g.getPrizes().size()) {
                                        for(User u : usr) {
                                            if(!u.isBot() && !u.isFake()) {
                                                winners.put(u, g.getPrizes().get(count));
                                                count++;
                                            }
                                        }
                                        break;
                                    }
                                    while(count < g.getPrizes().size()) {
                                        User u = usr.get(Utils.r.nextInt(usr.size()));
                                        if(u.isBot() || u.isFake()) {
                                            continue;
                                        }
                                        if(winners.containsKey(u)) {
                                            continue;
                                        }
                                        winners.put(u, g.getPrizes().get(count));
                                        count++;
                                    }
                                }
                            }

                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.RED);
                            eb.setTitle(EmojiList.MONEY + " " + g.getTitle());
                            eb.setDescription("Sorteio finalizado");
                            eb.setFooter("Iniciado por " + g.getAuthorName(), g.getAuthorAvatar());
                            String premios = "";

                            for(User u : winners.keySet()) {
                                Prize p = winners.get(u);
                                premios = premios + " - " + u.getName() + " | " + p.getName() + "\n";

                                if(p.getDmMessage().equalsIgnoreCase("Não definido")) {
                                    continue;
                                }

                                u.openPrivateChannel().queue(pc -> pc.sendMessage(p.getDmMessage()).queue());
                            }

                            eb.addField(":trophy: Ganhadores", premios ,false);

                            msg.editMessage(eb.build()).queue();

                            toCleanup.add(id);
                        }else{

                            Message msg = ch.getMessageById(g.getMessageID()).complete();

                            for(MessageReaction rc : msg.getReactions()) {
                                if(rc.getReactionEmote().getName().equalsIgnoreCase("⏭")) {
                                    List<User> usr = rc.getUsers().complete();

                                    for(User u : usr) {
                                        if(!Main.getDatabase().getGuildProfile(ch.getGuild()).hasPermission(ch.getGuild().getMember(u), "command.giveaway.takewinner")) {
                                            continue;
                                        }
                                        g.endNow();
                                    }
                                }
                            }

                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.GREEN);
                            eb.setTitle(EmojiList.MONEY + " " + g.getTitle());
                            eb.setDescription("Para participar clique na reação :white_check_mark:");
                            eb.setFooter("Iniciado por " + g.getAuthorName(), g.getAuthorAvatar());
                            String premios = "";

                            for(Prize p : g.getPrizes()) {
                                premios = premios + " - " + p.getName() + "\n";
                            }

                            eb.addField(":tada: Premios", premios, false);
                            eb.addField(":stopwatch: Tempo restante", Utils.getTime(g.getEndTime() - System.currentTimeMillis()), false);

                            msg.editMessage(eb.build()).queue();
                        }
                    }
                }

                if(toCleanup.size() > 0) {
                    toCleanup.forEach(s -> giveways.remove(s));
                    Main.getDatabase().getServer().updateGiveways(giveways);
                }

            }

        }, 0, 20, TimeUnit.SECONDS);
    }
    
}
