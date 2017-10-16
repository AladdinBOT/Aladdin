package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.beans.ConstructorProperties;
import java.io.InvalidObjectException;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class StarboardProfile {

    String emote;
    int amount;
    String channel_id;

    HashMap<String, String> messages = new HashMap<>();

    public StarboardProfile(String emote, int amount, String channel_id) {
        this(emote, amount, channel_id, new HashMap<>());
    }

    @ConstructorProperties({"emote", "amount", "channel_id", "messages"})
    public StarboardProfile(String emote, int amount, String channel_id, HashMap<String, String> messages) {
        this.emote = emote;
        this.amount = amount;
        this.channel_id = channel_id;
        this.messages = messages;
    }

    public boolean removeReaction(MessageReactionRemoveEvent e) throws InvalidObjectException {
        String emote;

        if(e.getReactionEmote().getId() != null) {
            emote = "<:" + e.getReactionEmote().getName() + ":" + e.getReactionEmote().getId() + ">";
        }else{
            emote = e.getReactionEmote().getName();
        }

        Message msg = e.getTextChannel().getMessageById(e.getMessageId()).complete();

        if(msg == null) {
            return false;
        }

        MessageReaction reaction = Utils.findReaction(e.getReactionEmote().getName(), e.getReactionEmote().getId(), msg);

        TextChannel ch = e.getGuild().getTextChannelById(channel_id);

        if(ch == null) {
            throw new InvalidObjectException("The main channnel is invalid, delete this starboard now.");
        }

        if(reaction == null) {
            if(messages.containsKey(e.getMessageId())) {
                Message from = ch.getMessageById(messages.get(e.getMessageId())).complete();
                if(from != null)
                    from.delete().queue();

                messages.remove(e.getMessageId());
                return true;
            }
            return false;
        }

        if(reaction.getCount() < amount) {
            if(messages.containsKey(e.getMessageId())) {
                Message from = ch.getMessageById(messages.get(e.getMessageId())).complete();
                if(from != null)
                    from.delete().queue();

                messages.remove(e.getMessageId());
                return true;
            }
            return true;
        }

        Message m = ch.getMessageById(messages.get(e.getMessageId())).complete();
        if(m == null) {
            return false;
        }

        EmbedBuilder b = new EmbedBuilder(m.getEmbeds().get(0));
        MessageEmbed.AuthorInfo old = m.getEmbeds().get(0).getAuthor();
        b.setAuthor(old.getName().replace((reaction.getCount() + 1) + "", reaction.getCount() + ""), null, old.getUrl());

        m.editMessage(b.build()).queue();
        return false;
    }

    public boolean addReaction(MessageReactionAddEvent e) throws InvalidObjectException {
        String emote;

        if(e.getReactionEmote().getId() != null) {
            emote = "<:" + e.getReactionEmote().getName() + ":" + e.getReactionEmote().getId() + ">";
        }else{
            emote = e.getReactionEmote().getName();
        }

        Message msg = e.getTextChannel().getMessageById(e.getMessageId()).complete();

        if(msg == null) {
            return false;
        }

        MessageReaction reaction = Utils.findReaction(e.getReactionEmote().getName(), e.getReactionEmote().getId(), msg);

        if(reaction == null) {
            return false;
        }

        if(reaction.getCount() < amount) {
            return false;
        }

        TextChannel ch = e.getGuild().getTextChannelById(channel_id);

        if(ch == null) {
            throw new InvalidObjectException("The main channnel is invalid, delete this starboard now.");
        }

        if(messages.containsKey(e.getMessageId())) {

            Message m = ch.getMessageById(messages.get(e.getMessageId())).complete();
            if(m == null) {
                return false;
            }

            EmbedBuilder b = new EmbedBuilder(m.getEmbeds().get(0));
            MessageEmbed.AuthorInfo old = m.getEmbeds().get(0).getAuthor();
            b.setAuthor(old.getName().replace((reaction.getCount() - 1) + "", reaction.getCount() + ""), null, old.getUrl());

            m.editMessage(b.build()).queue();

            return false;
        }

        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setAuthor(emote + reaction.getCount() + " | Enviada por " + msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl());
        b.setDescription(msg.getContent());

        if(msg.getAttachments().size() > 0 && msg.getAttachments().get(0).isImage()) {
            b.setImage(msg.getAttachments().get(0).getUrl());
        }

        b.setFooter("Enviada em #" + e.getChannel().getName(), null);
        b.setTimestamp(msg.getCreationTime());

        Message newm = ch.sendMessage(b.build()).complete();

        if(newm != null) {
            messages.put(e.getMessageId(), newm.getId());
            return true;
        }

        return false;
    }

}
