package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

import java.awt.*;
import java.beans.ConstructorProperties;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class StarboardProfile {

    String emote;
    HashMap<Long, Long> messages = new HashMap<>();
    long channel_id;

    int amount;

    public StarboardProfile(String emote, long channel_id, int amount) {
        this(emote, new HashMap<>(), channel_id, amount);
    }

    @ConstructorProperties({"emote", "messages", "channel_id", "amout"})
    public StarboardProfile(String emote, HashMap<Long, Long> messages, long channel_id, int amount) {
        this.emote = emote;
        this.messages = messages;
        this.channel_id = channel_id;
        this.amount = amount;
    }

    public boolean removeFromStarboard(MessageReactionRemoveEvent e) {
        if(!messages.containsKey(e.getMessageIdLong())) {
            return false;
        }
        String emt = e.getReactionEmote().getName() + "|" + (e.getReactionEmote().getId() == null ? "null" : e.getReactionEmote().getId());
        if(emt.equals(emote)) {
            if (e.getReaction().getCount() < amount) {
                TextChannel ch = e.getGuild().getTextChannelById(channel_id);
                if(ch == null || !ch.canTalk()) {
                    messages.remove(e.getMessageIdLong());
                    return true;
                }

                ch.getMessageById(messages.get(e.getMessageIdLong())).queue(msg -> msg.delete().queue());
                return true;
            }
        }
        return false;
    }

    public boolean addToStarboard(MessageReactionAddEvent e) {
        String emt = e.getReactionEmote().getName() + "|" + (e.getReactionEmote().getId() == null ? "null" : e.getReactionEmote().getId());
        if(emt.equals(emote)) {
            if(e.getReaction().getCount() < amount) {
                return false;
            }

            TextChannel ch = e.getGuild().getTextChannelById(channel_id);
            if(ch == null || !ch.canTalk()) {
                return false;
            }

            if(!messages.containsKey(e.getMessageIdLong())) {

                String emote = "";

                if(e.getReactionEmote().getId() == null) {
                    emote = e.getReactionEmote().getName();
                }else{
                    emote = "<:" + e.getReactionEmote().getName() + ":" + e.getReactionEmote().getId() + ">";
                }

                Message msg = e.getTextChannel().getMessageById(e.getMessageIdLong()).complete();
                if(msg == null) {
                    return false;
                }

                User author = msg.getAuthor();
                if(author == null) {
                    return false;
                }

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setAuthor(emote + " " + e.getReaction().getCount() + " | Enviada por " + author.getName(), null, author.getEffectiveAvatarUrl());
                b.setDescription(msg.getContent());

                if(msg.getAttachments().size() > 0) {
                    if(msg.getAttachments().get(0).isImage()) {
                        b.setImage(msg.getAttachments().get(0).getUrl());
                    }
                }

                b.setFooter("Enviada em #" + e.getTextChannel().getName(), null);
                b.setTimestamp(msg.getCreationTime());

                Message embed = ch.sendMessage(b.build()).complete();

                if(embed != null) {
                    messages.put(e.getMessageIdLong(), embed.getIdLong());
                    return true;
                }

                return false;
            }

            Message embed = ch.getMessageById(messages.get(e.getMessageIdLong())).complete();

            if(embed == null) {
                messages.remove(e.getMessageIdLong());
                return false;
            }

            EmbedBuilder b = new EmbedBuilder(embed.getEmbeds().get(0));
            b.setTitle(embed.getEmbeds().get(0).getTitle().replace((e.getReaction().getCount() - 1) + "", e.getReaction().getCount() + ""));

            embed.editMessage(b.build()).queue();
            return true;
        }
        return false;
    }

}
