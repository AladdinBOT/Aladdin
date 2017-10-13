package net.heyzeer0.aladdin.profiles.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
import net.heyzeer0.aladdin.database.entities.UserProfile;
import net.heyzeer0.aladdin.enums.EmojiList;

import javax.xml.soap.Text;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MessageEvent {

    GuildMessageReceivedEvent originEvent;
    ArrayList<Message> sended = new ArrayList<>();

    public MessageEvent(GuildMessageReceivedEvent origin) {
        originEvent = origin;
    }

    public void sendMessage(String msg) {
        if(msg.equalsIgnoreCase("")) {
            return;
        }
        originEvent.getChannel().sendMessage(msg).queue(newer -> sended.add(newer));
    }

    public void sendMessage(EmbedBuilder b) {
        sendMessage(new MessageBuilder().setEmbed(b.build()).build());
    }

    public RestAction<Message> sendPureMessage(String msg) {
        return originEvent.getChannel().sendMessage(msg);
    }

    public void sendMessage(Message msg) {
        originEvent.getChannel().sendMessage(msg).queue(newer -> sended.add(newer), err -> {if(err instanceof InsufficientPermissionException) {sendMessage(EmojiList.WORRIED + " Oops, é necessário a permissao ``" + ((InsufficientPermissionException)err).getPermission().toString() + "`` para executar este comando.");}});
    }

    public RestAction<Message> sendPureMessage(Message msg) {
        return originEvent.getChannel().sendMessage(msg);
    }

    public void sendPrivateMessage(String msg) {
        originEvent.getAuthor().openPrivateChannel().queue(prv -> prv.sendMessage(msg).queue(scs -> {sended.add(scs);}));
    }

    public void sendPrivateMessage(Message msg) {
        originEvent.getAuthor().openPrivateChannel().queue(prv -> prv.sendMessage(msg).queue(scs -> {sended.add(scs);}));
    }

    public GuildMessageReceivedEvent getOriginEvent() {
        return originEvent;
    }

    public ChannelType getChannelType() {
        return originEvent.getChannelType();
    }

    public void deleteMessage() {
        originEvent.getMessage().delete().queue();
    }

    public String getMessageContent() {
        return originEvent.getMessage().getContent();
    }

    public User getAuthor() {
        return originEvent.getAuthor();
    }

    public Message getMessage() {
        return originEvent.getMessage();
    }

    public TextChannel getChannel() {
        return originEvent.getChannel();
    }

    public JDA getJDA() {
        return originEvent.getJDA();
    }

    public Guild getGuild() {
        return originEvent.getGuild();
    }

    public Member getMember() {
        return originEvent.getMember();
    }

    public void deleteMessages() {
        originEvent.getMessage().delete().queue(scs -> {}, failure -> {});
        if(sended.size() >= 1) {
            sended.forEach(newer -> newer.delete().queue());
        }
    }

    public boolean hasPermission(String x) { return getGuildProfile().hasPermission(originEvent.getMember(), x); }

    public GuildProfile getGuildProfile() { return Main.getDatabase().getGuildProfile(originEvent.getGuild()); }

    public UserProfile getUserProfile() { return Main.getDatabase().getUserProfile(originEvent.getAuthor()); }

}
