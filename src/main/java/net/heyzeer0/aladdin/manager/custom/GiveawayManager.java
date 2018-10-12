/*
 * Developed by HeyZeer0 on 10/12/18 11:10 AM.
 * Last Modification 10/12/18 11:10 AM.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.manager.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.profiles.GiveawayProfile;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.profiles.utilities.ScheduledExecutor;
import net.heyzeer0.aladdin.utils.RandomSeed;
import net.heyzeer0.aladdin.utils.Utils;
import net.heyzeer0.aladdin.utils.builders.GiveawayBuilder;
import net.heyzeer0.aladdin.utils.builders.Prize;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GiveawayManager {

    public static HashMap<String, GiveawayProfile> giveways = new HashMap<>();
    public static boolean already_requested = false;

    private static final RandomSeed random = new RandomSeed();

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
        eb.addField(":stopwatch: Tempo restante", Utils.getTime(b.getEnd_time(), Main.getDatabase().getGuildProfile(b.getCh().getGuild()).getSelectedLanguage().getLangProfile()), false);

        Message msg = b.getCh().sendMessage(eb.build()).complete();

        if(msg != null) {
            msg.addReaction("✅").complete();
            giveways.put(msg.getId(), new GiveawayProfile(msg.getId(), b.getCh().getId(), b.getCh().getGuild().getId(), b.getE().getAuthor().getName(), b.getE().getAuthor().getEffectiveAvatarUrl(), b.getName(), b.getPrizes(), System.currentTimeMillis() + b.getEnd_time()));
            Main.getDatabase().getServer().updateGiveways(giveways);
        }
    }

    public static void startUpdating() {
        ThreadManager.registerScheduledExecutor(new ScheduledExecutor(20000, () -> {
            if(giveways.size() <= 0) {
                if(!already_requested) {
                    giveways = Main.getDatabase().getServer().getGiveaways();
                    giveways.keySet().stream().filter(c -> c.equals("491031048306819072")).findFirst().ifPresent(c -> giveways.remove(c));

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
                            toCleanup.add(id);
                            Message msg = ch.getMessageById(g.getMessageID()).complete();

                            HashMap<User, Prize> winners = new HashMap<>();
                            ArrayList<Integer> seeds = new ArrayList<>();

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
                                        User u = usr.get(random.nextInt(usr.size()));
                                        if(u.isBot() || u.isFake()) {
                                            continue;
                                        }
                                        if(winners.containsKey(u)) {
                                            continue;
                                        }

                                        random.getLastGeneratedNumber().ifPresent(seeds::add);
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

                            String seeds_string = "";
                            for(Integer l : seeds) {
                                if(seeds_string.equals("")) {
                                    seeds_string = l.toString();
                                    continue;
                                }

                                seeds_string = seeds_string + ", " + l.toString();
                            }

                            eb.addField(":scroll: Seeds", "``" + seeds_string + "``", false);

                            msg.editMessage(eb.build()).queue();
                        }else{
                            toCleanup.add(id);

                            try{
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
                                eb.addField(":stopwatch: Tempo restante", Utils.getTime(g.getEndTime() - System.currentTimeMillis(), Main.getDatabase().getGuildProfile(msg.getGuild()).getSelectedLanguage().getLangProfile()), false);

                                msg.editMessage(eb.build()).queue();
                            }catch (Exception ex) { }
                        }
                    }
                }

                if(toCleanup.size() > 0) {
                    toCleanup.forEach(s -> giveways.remove(s));
                    Main.getDatabase().getServer().updateGiveways(giveways);
                }

            }
        }));
    }
    
}
