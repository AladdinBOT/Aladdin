/*
 * Developed by HeyZeer0 on 11/20/18 1:01 PM.
 * Last Modification 11/20/18 12:58 PM.
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
import java.util.HashSet;
import java.util.List;

public class GiveawayManager {

    public static HashMap<String, GiveawayProfile> giveaways = new HashMap<>();
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
            giveaways.put(msg.getId(), new GiveawayProfile(msg.getId(), b.getCh().getId(), b.getCh().getGuild().getId(), b.getE().getAuthor().getName(), b.getE().getAuthor().getEffectiveAvatarUrl(), b.getName(), b.getPrizes(), System.currentTimeMillis() + b.getEnd_time()));
        }
    }

    public static void startUpdating() {
        ThreadManager.registerScheduledExecutor(new ScheduledExecutor(20000, () -> {
            if(giveaways.size() <= 0) {
                giveaways = Main.getDatabase().getServer().getGiveaways();
                return;
            }

            //this will handle the giveaway cleanup, add a giveaway here and it will be cleaned
            HashSet<String> toRemove = new HashSet<>();

            for(String messageId : giveaways.keySet()) {
                GiveawayProfile gp = giveaways.get(messageId);

                TextChannel ch = Main.getTextChannelById(gp.getChannelID());

                //check if the giveaway exists by getting the textChannel
                if(ch == null) {
                    toRemove.add(messageId);
                    continue;
                }

                Message m;

                //check if the message exists (first one is getting the message, second one is to garantee that the message is not null)
                try{
                    m = ch.getMessageById(messageId).complete();
                }catch (Exception ex) { toRemove.add(messageId); continue; }

                if(m == null) { toRemove.add(messageId);continue; }

                //if the giveaway ended, finish it
                if(gp.getEndTime() - System.currentTimeMillis() <= 0) {
                    toRemove.add(messageId);

                    HashMap<User, Prize> winners = new HashMap<>();
                    ArrayList<Integer> seeds = new ArrayList<>();

                    if(m.getReactions().size() <= 1) continue;

                    //process winners
                    for(MessageReaction rc : m.getReactions()) {
                        if(rc.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                            List<User> possibleWinners;

                            //check if there is possible winners and set it
                            try{
                                possibleWinners = rc.getUsers().complete();
                            }catch (Exception ex) { break; }

                            if(possibleWinners == null) break;

                            //if there is no enough users, give to all of them the "prize"
                            if(possibleWinners.size() <= gp.getPrizes().size()) {
                                for(int i = 0; i < possibleWinners.size(); i++) {
                                    winners.put(possibleWinners.get(i), gp.getPrizes().get(i));
                                }
                                break;
                            }

                            //now make the prize random if there is more users than prize amount
                            int selectedWinners = 0;
                            while(selectedWinners < gp.getPrizes().size()) {
                                User u = possibleWinners.get(random.nextInt(possibleWinners.size()));
                                if(u.isBot() || u.isFake() || winners.containsKey(u)) continue;

                                random.getLastGeneratedNumber().ifPresent(seeds::add);
                                winners.put(u, gp.getPrizes().get(selectedWinners));
                                selectedWinners++;
                            }
                        }
                    }

                    //changing the embed
                    EmbedBuilder eb = new EmbedBuilder(); eb.setColor(Color.RED); eb.setTitle(EmojiList.MONEY + " " + gp.getTitle()); eb.setDescription("Sorteio finalizado"); eb.setFooter("Iniciado por " + gp.getAuthorName(), gp.getAuthorAvatar());

                    String prize = "";

                    for(User u : winners.keySet()) {
                        Prize p = winners.get(u);
                        prize = prize + " - " + u.getName() + " | " + p.getName() + "\n";

                        if(p.getDmMessage().equalsIgnoreCase("Não definido")) continue;

                        u.openPrivateChannel().queue(pc -> pc.sendMessage(p.getDmMessage()).queue());
                    }

                    eb.addField(":trophy: Ganhadores", prize ,false);

                    String seeds_string = "";
                    for(Integer l : seeds) {
                        if(seeds_string.equals("")) {
                            seeds_string = l.toString();
                            continue;
                        }

                        seeds_string = seeds_string + ", " + l.toString();
                    }

                    eb.addField(":scroll: Seeds", "``" + seeds_string + "``", false);

                    //updating the message, and checking if you can edit it
                    m.editMessage(eb.build()).queue(c -> {}, f -> toRemove.add(messageId));
                    continue;
                }

                //update the embed message.
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.GREEN);
                eb.setTitle(EmojiList.MONEY + " " + gp.getTitle());
                eb.setDescription("Para participar clique na reação :white_check_mark:");
                eb.setFooter("Iniciado por " + gp.getAuthorName(), gp.getAuthorAvatar());
                String premios = "";

                for(Prize p : gp.getPrizes()) {
                    premios = premios + " - " + p.getName() + "\n";
                }

                eb.addField(":tada: Premios", premios, false);
                eb.addField(":stopwatch: Tempo restante", Utils.getTime(gp.getEndTime() - System.currentTimeMillis(), Main.getDatabase().getGuildProfile(m.getGuild()).getSelectedLanguage().getLangProfile()), false);

                m.editMessage(eb.build()).queue(s -> {}, f -> toRemove.add(messageId));
            }

            if(toRemove.size() >= 1) toRemove.forEach(giveaways::remove);
            Main.getDatabase().getServer().updateGiveaways(giveaways);
        }));
    }
    
}
