package net.heyzeer0.aladdin.profiles.utilities;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.manager.utilities.PaginatorManager;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 11/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Paginator {

    public static final String FIRST = "⏮";
    public static final String BACK = "◀";
    public static final String NEXT = "▶";
    public static final String END = "⏭";
    public static final String STOP = "⏹";

    MessageEvent e;
    String title;
    String owner;

    Integer pamount = 0;
    HashMap<Integer, String> pages = new HashMap<>();

    Integer actual = 1;
    String actual_id;

    float last_use = 0;

    public Paginator(MessageEvent e, String title) {
        this.e = e;
        this.title = title;
        owner = e.getAuthor().getId();
    }


    public void addPage(String x) {
        pamount++;
        pages.put(pamount, x);
    }

    public void start() {
        last_use = System.currentTimeMillis();

        Message m = e.getChannel().sendMessage(new MessageBuilder().setEmbed(getEmbed()).build()).complete();

        if(m != null) {
            actual_id = m.getId();

            PaginatorManager.registerPaginator(this);


            if(pamount == 1) {
                m.addReaction(STOP).complete();
                return;
            }

            m.addReaction(NEXT).complete();
            m.addReaction(END).complete();
            m.addReaction(STOP).complete();
        }

    }

    public MessageEmbed getEmbed() {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setTitle(title + " | Pag " + actual + "/" + pamount);
        b.setDescription("```" + pages.get(actual) + "```");

        return b.build();
    }

    public void changePage(MessageReactionAddEvent e) {
        if(e.getMessageId().equals(actual_id) && e.getMember().getUser().getId().equals(owner)) {
            if(e.getReaction().getEmote().getName().equals(NEXT) && actual != pamount) {
                e.getChannel().getMessageById(actual_id).complete().delete().complete();
                actual++;
                Message m = e.getChannel().sendMessage(getEmbed()).complete();
                if(m != null) {
                    last_use = System.currentTimeMillis();
                    actual_id = m.getId();


                    if(actual == pamount) {
                        m.addReaction(FIRST).complete();
                        m.addReaction(BACK).complete();
                        m.addReaction(STOP).complete();
                    }else{
                        m.addReaction(FIRST).complete();
                        m.addReaction(BACK).complete();
                        m.addReaction(NEXT).complete();
                        m.addReaction(END).complete();
                        m.addReaction(STOP).complete();
                    }
                }
                return;
            }
            if(e.getReaction().getEmote().getName().equals(BACK) && actual != 1) {
                e.getChannel().getMessageById(actual_id).complete().delete().complete();
                actual--;
                Message m = e.getChannel().sendMessage(getEmbed()).complete();
                if(m != null) {
                    last_use = System.currentTimeMillis();
                    actual_id = m.getId();

                    if(actual == 1) {
                        m.addReaction(NEXT).complete();
                        m.addReaction(END).complete();
                        m.addReaction(STOP).complete();
                    }else{
                        m.addReaction(FIRST).complete();
                        m.addReaction(BACK).complete();
                        m.addReaction(NEXT).complete();
                        m.addReaction(END).complete();
                        m.addReaction(STOP).complete();
                    }
                }
                return;
            }
            if(e.getReaction().getEmote().getName().equals(FIRST)) {
                e.getChannel().getMessageById(actual_id).complete().delete().complete();
                actual = 1;
                Message m = e.getChannel().sendMessage(getEmbed()).complete();
                if(m != null) {
                    last_use = System.currentTimeMillis();
                    actual_id = m.getId();

                    if(actual == 1) {
                        m.addReaction(NEXT).complete();
                        m.addReaction(END).complete();
                        m.addReaction(STOP).complete();
                    }
                }
                return;
            }

            if(e.getReaction().getEmote().getName().equals(END)) {
                e.getChannel().getMessageById(actual_id).complete().delete().complete();
                actual = pamount;
                Message m = e.getChannel().sendMessage(getEmbed()).complete();
                if(m != null) {
                    last_use = System.currentTimeMillis();
                    actual_id = m.getId();

                    if(actual == pamount) {
                        m.addReaction(FIRST).complete();
                        m.addReaction(BACK).complete();
                        m.addReaction(STOP).complete();
                    }
                }
                return;
            }
            if(e.getReaction().getEmote().getName().equals(STOP)) {
                PaginatorManager.paginators.remove(getActualId());
                e.getChannel().getMessageById(getActualId()).complete().delete().complete();
            }
        }
    }

    public boolean clear() {
        if(System.currentTimeMillis() - last_use >= 15000) {
            e.getChannel().getMessageById(getActualId()).complete().delete().complete();
            return true;
        }
        return false;
    }


    public String getActualId() {
        return actual_id;
    }


}
