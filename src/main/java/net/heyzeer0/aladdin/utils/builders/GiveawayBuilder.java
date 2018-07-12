package net.heyzeer0.aladdin.utils.builders;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.manager.custom.GiveawayManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 17/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class GiveawayBuilder {

    public static HashMap<String, GiveawayBuilder> builders = new HashMap<>();

    public static boolean checkMessages(GuildMessageReceivedEvent e) {
        if(builders.size() >= 1) {
            for(String x : builders.keySet()) {
                if(x.equalsIgnoreCase(e.getAuthor().getId())) {
                    GiveawayBuilder b = builders.get(x);
                    if(b.e.getChannel().getId().equalsIgnoreCase(e.getChannel().getId())) {
                        builders.get(x).receiveTextUpdate(new MessageEvent(e));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void checkReaction(MessageReactionAddEvent e) {
        if(builders.size() >= 1) {
            for(String x : builders.keySet()) {
                if(x.equalsIgnoreCase(e.getUser().getId())) {
                    builders.get(x).receiveClickUpdate(e);
                }
            }
        }
    }

    String name;
    long end_time = 0L;
    TextChannel ch;
    MessageEvent e;
    ArrayList<Prize> prizes = new ArrayList<>();

    String builder_message_id;
    ActualPhase phase = ActualPhase.MAIN_FRAME;
    Prize addPrize;
    String last_text_message_id;

    LangProfile lp;

    public GiveawayBuilder(MessageEvent e, LangProfile lp) {
        this.e = e; this.lp = lp;
        name = lp.get("command.giveaway.builder.notset");

        updateMessage(e);
    }

    private void updateMessage(MessageEvent e) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);

        if(phase == ActualPhase.ADDING_PRIZE) {
            b.setTitle(lp.get("command.giveaway.builder.embed.prize.title"));
            b.setDescription(String.format(lp.get("command.giveaway.builder.embed.prize.description"), addPrize.getName(), addPrize.getDmMessage()));
        }else{
            b.setTitle(lp.get("command.giveaway.builder.embed.main.title"));
            b.setDescription(String.format(lp.get("command.giveaway.builder.embed.main.description"), name, (end_time == 0 ? lp.get("command.giveaway.builder.notset") : Utils.getTime(end_time, e.getGuildProfile().getSelectedLanguage().getLangProfile())), prizes.size(), (ch == null ? lp.get("command.giveaway.builder.notset") : "#" + ch.getName())));
        }

        b.setFooter(lp.get("command.giveaway.builder.embed.footer") + " " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
        b.setTimestamp(e.getMessage().getCreationTime());


        if(last_text_message_id != null) {
            e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
            last_text_message_id = null;
        }

        if(builder_message_id != null) {
            e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
        }

        Message msg = e.getChannel().sendMessage(b.build()).complete();

        if(msg != null) {
            builder_message_id = msg.getId();

            if(phase == ActualPhase.ADDING_PRIZE) {
                msg.addReaction(Utils.getRegional("1")).queue();
                msg.addReaction(Utils.getRegional("2")).queue();
                msg.addReaction("✅").queue();
                msg.addReaction("\uD83D\uDED1").queue();
            }else {
                msg.addReaction(Utils.getRegional("1")).queue();
                msg.addReaction(Utils.getRegional("2")).queue();
                msg.addReaction(Utils.getRegional("3")).queue();
                msg.addReaction(Utils.getRegional("4")).queue();
                msg.addReaction("✅").queue();
                msg.addReaction("\uD83D\uDED1").queue();
            }

            if(!builders.containsKey(e.getAuthor().getId())) {
                builders.put(e.getAuthor().getId(), this);
            }

            this.e = e;
        }
    }

    public void receiveClickUpdate(MessageReactionAddEvent ev) {
        if(!ev.getChannel().getId().equals(e.getChannel().getId()) && !e.getAuthor().getId().equals(ev.getUser().getId())) {
            return;
        }

        if(phase == ActualPhase.ADDING_PRIZE) {

            if(ev.getReactionEmote().getName().equals("✅")) {

                if(addPrize.getName().equalsIgnoreCase(lp.get("command.giveaway.builder.notset"))) {
                    ev.getTextChannel().sendMessage(lp.get("command.giveaway.builder.prize.notitleset")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                prizes.add(addPrize);
                addPrize = null;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("\uD83D\uDED1")) {
                addPrize = null;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("1⃣")) {
                phase = ActualPhase.WAITING_FOR_PRIZE_NAME;

                Message msg = e.getChannel().sendMessage(lp.get("command.giveaway.builder.prize.set.title")).complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("2⃣")) {
                phase = ActualPhase.WAITING_FOR_PRIZE_DM;

                Message msg = e.getChannel().sendMessage(lp.get("command.giveaway.builder.prize.set.message")).complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }

            return;
        }
        if(phase == ActualPhase.MAIN_FRAME) {

            if(ev.getReactionEmote().getName().equals("✅")) {

                if(name.equalsIgnoreCase(lp.get("command.giveaway.builder.notset"))) {
                    ev.getTextChannel().sendMessage(lp.get("command.giveaway.builder.main.notitleset")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(end_time == 0) {
                    ev.getTextChannel().sendMessage(lp.get("command.giveaway.builder.main.notimeset")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(ch == null) {
                    ev.getTextChannel().sendMessage(lp.get("command.giveaway.builder.main.nochannelset")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(prizes.size() <= 0) {
                    ev.getTextChannel().sendMessage(lp.get("command.giveaway.builder.main.noprizeset")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                if(last_text_message_id != null) {
                    e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
                    last_text_message_id = null;
                }

                if(builder_message_id != null) {
                    e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
                }

                builders.remove(e.getAuthor().getId());

                e.sendMessage(lp.get("command.giveaway.builder.success"));
                GiveawayManager.createGiveway(this);
                return;
            }
            if(ev.getReactionEmote().getName().equals("\uD83D\uDED1")) {
                if(last_text_message_id != null) {
                    e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
                    last_text_message_id = null;
                }

                if(builder_message_id != null) {
                    e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
                }

                builders.remove(e.getAuthor().getId());
                return;
            }
            if(ev.getReactionEmote().getName().equals("1⃣")) {
                phase = ActualPhase.WAITING_FOR_TITLE;

                Message msg = e.getChannel().sendMessage(lp.get("command.giveaway.builder.main.set.title")).complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("2⃣")) {
                phase = ActualPhase.WAITING_FOR_TIME;

                Message msg = e.getChannel().sendMessage(lp.get("command.giveaway.builder.main.set.time")).complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("3⃣")) {
                phase = ActualPhase.ADDING_PRIZE;
                addPrize = new Prize(lp.get("command.giveaway.builder.notset"), lp.get("command.giveaway.builder.notset"));

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("4⃣")) {
                phase = ActualPhase.WAITING_FOR_CHANNEL;

                Message msg = e.getChannel().sendMessage(lp.get("command.giveaway.builder.main.set.channel")).complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }

            return;
        }
    }

    public void receiveTextUpdate(MessageEvent ev) {
        if(!ev.getChannel().getId().equals(e.getChannel().getId())) {
            return;
        }

        if(ev.getMessage().getContentDisplay().equalsIgnoreCase(lp.get("command.giveaway.builder.cancelcommand"))) {
            if(phase == ActualPhase.WAITING_FOR_PRIZE_NAME || phase == ActualPhase.WAITING_FOR_PRIZE_DM) {
                phase = ActualPhase.ADDING_PRIZE;
            }else{
                phase = ActualPhase.MAIN_FRAME;
            }

            ev.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
            return;
        }

        if(phase == ActualPhase.WAITING_FOR_CHANNEL) {
            if(ev.getMessage().getMentionedChannels().size() <= 0) {
                e.getChannel().sendMessage(lp.get("command.giveaway.builder.main.set.channel.error.channel.invalid")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                return;
            }

            ch = ev.getMessage().getMentionedChannels().get(0);
            phase = ActualPhase.MAIN_FRAME;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_PRIZE_DM) {
            addPrize.setDmMessage(ev.getMessage().getContentRaw());
            phase = ActualPhase.ADDING_PRIZE;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_PRIZE_NAME) {
            addPrize.setName(ev.getMessage().getContentRaw());
            phase = ActualPhase.ADDING_PRIZE;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_TIME) {
            boolean minute = ev.getMessage().getContentDisplay().contains("m");

            try{

                Integer value = Integer.valueOf(ev.getMessage().getContentDisplay().replace("m", "").replace("h", ""));

                if(!minute && !ev.getUserProfile().isPremiumActive()) {
                    if(value > 24) {
                        ev.getChannel().sendMessage(String.format(lp.get("command.giveaway.builder.main.set.time.error.timelimit"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                        return;
                    }
                }
                if(minute && !ev.getUserProfile().isPremiumActive()) {
                    if(value > 1440) {
                        ev.getChannel().sendMessage(String.format(lp.get("command.giveaway.builder.main.set.time.error.timelimit"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium")).queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                        return;
                    }
                }

                long time = (minute ? (60000 * value) : (3600000 * value));

                end_time = time;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(ev);

            }catch (Exception ex) {
                e.sendMessage(lp.get("command.giveaway.builder.main.set.time.error.invalid"));
            }
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_TITLE) {
            name = ev.getMessage().getContentRaw();
            phase = ActualPhase.MAIN_FRAME;

            updateMessage(ev);
            return;
        }
    }

    enum ActualPhase {
        MAIN_FRAME, ADDING_PRIZE, WAITING_FOR_CHANNEL, WAITING_FOR_TITLE, WAITING_FOR_TIME, WAITING_FOR_PRIZE_NAME, WAITING_FOR_PRIZE_DM
    }

}
