package net.heyzeer0.aladdin.manager.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.profiles.GivewayProfile;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;
import org.apache.commons.lang3.StringUtils;

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

    public static HashMap<String, GivewayProfile> giveways = new HashMap<>();
    public static boolean already_requested = false;

    private static ScheduledExecutorService giveTimer = Executors.newSingleThreadScheduledExecutor();

    public static void createGiveway(String description, long time, int winnerAmount, MessageEvent e) {
        if(!e.getChannel().canTalk()) {
            return;
        }

        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setAuthor("Sorteio iniciado por " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), null, e.getAuthor().getEffectiveAvatarUrl());
        b.setDescription("Para participar clique na reação :white_check_mark:\n\n" + description);
        b.addField(":alarm_clock: Tempo restante ", Utils.getTime(time - System.currentTimeMillis()), true);
        b.setFooter(winnerAmount + (winnerAmount > 1 ? " Vencedores" : " Vencedor"), null);

        Message msg = e.sendPureMessage(new MessageBuilder().setEmbed(b.build()).build()).complete();

        if(msg != null) {
            msg.addReaction("✅").complete();
            giveways.put(msg.getId(), new GivewayProfile(msg.getId(), msg.getChannel().getId(), msg.getGuild().getId(), description, e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl(), time, winnerAmount));
            Main.getDatabase().getServer().updateGiveways(giveways);
        }
    }

    public static void startUpdating() {
        giveTimer.scheduleAtFixedRate(() -> {

            if(giveways.size() < 0) {
                if(!already_requested) {
                    giveways = Main.getDatabase().getServer().getGiveways();
                    already_requested = true;
                }
            }else{

                ArrayList<String> toCleanup = new ArrayList<>();

                for(String id : giveways.keySet()) {

                    GivewayProfile g = giveways.get(id);
                    TextChannel ch = Main.getGuildById(g.getGuildID()).getTextChannelById(g.getChannelID());

                    if(ch == null || ch.getMessageById(g.getMessageID()).complete() == null) {
                        toCleanup.add(id);
                    }else {
                        if(g.getEndTime() - System.currentTimeMillis() <= 0) {

                            Message msg = ch.getMessageById(g.getMessageID()).complete();

                            ArrayList<String> winners_mention = new ArrayList<>();

                            for(MessageReaction rc : msg.getReactions()) {
                                if(rc.getEmote().getName().equalsIgnoreCase("✅")) {
                                    List<User> usr = rc.getUsers().complete();
                                    Integer count = 0;
                                    if(usr.size() <= g.getWinnerAmount()) {
                                        for(User u : usr) {
                                            if(!u.isBot() && !u.isFake()) {
                                                winners_mention.add(u.getAsMention());
                                            }
                                        }
                                        break;
                                    }
                                    while(count < g.getWinnerAmount()) {
                                        User u = usr.get(Utils.r.nextInt(usr.size()));
                                        if(u.isBot() || u.isFake()) {
                                            continue;
                                        }
                                        if(winners_mention.contains(u.getAsMention())) {
                                            continue;
                                        }
                                        count++;
                                        winners_mention.add(u.getAsMention());
                                    }
                                }
                            }

                            String winner_mention = StringUtils.join(winners_mention, ", ");

                            EmbedBuilder b = new EmbedBuilder();
                            b.setColor(Color.RED);
                            b.setAuthor("Sorteio iniciado por " + g.getAuthorName(), null, g.getAuthorAvatar());
                            b.setDescription("Sorteio finalizado\n\n" + g.getDescription());
                            b.addField(":trophy: Vencedores ", winner_mention, true);
                            b.setFooter(g.getWinnerAmount() + (g.getWinnerAmount() > 1 ? " Vencedores" : " Vencedor"), null);

                            msg.editMessage(b.build()).queue();

                            toCleanup.add(id);
                        }else{

                            EmbedBuilder b = new EmbedBuilder();
                            b.setColor(Color.GREEN);
                            b.setAuthor("Sorteio iniciado por " + g.getAuthorName(), null, g.getAuthorAvatar());
                            b.setDescription("Para participar clique na reação :white_check_mark:\n\n" + g.getDescription());
                            b.addField(":stopwatch: Tempo restante ", Utils.getTime(g.getEndTime() - System.currentTimeMillis()), true);
                            b.setFooter(g.getWinnerAmount() + (g.getWinnerAmount() > 1 ? " Vencedores" : " Vencedor"), null);

                            ch.getMessageById(g.getMessageID()).queue(msg -> msg.editMessage(b.build()).queue());
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
