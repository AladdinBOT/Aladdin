package net.heyzeer0.aladdin.events;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.LogModules;

import java.awt.*;
import java.util.Optional;

/**
 * Created by HeyZeer0 on 27/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class LogEvents implements EventListener {

    private static final Cache<String, Optional<CachedMessage>> messageCache = CacheBuilder.newBuilder().concurrencyLevel(10).maximumSize(35000).build();

    @Override
    public void onEvent(Event e) {
        if(e instanceof GenericGuildMessageEvent) {
            if(!isModuleActive(((GenericGuildMessageEvent) e).getGuild(), LogModules.MESSAGE_MODULE)) {
                return;
            }
            if(e instanceof GuildMessageReceivedEvent) {
                GuildMessageReceivedEvent ev = (GuildMessageReceivedEvent)e;
                messageCache.put(((GuildMessageReceivedEvent) e).getMessageId(), Optional.of(new CachedMessage(ev.getMessage().getContent(), ev.getAuthor().getName(), ev.getAuthor().getEffectiveAvatarUrl(), ev.getAuthor().getId())));
                return;
            }
            if(e instanceof GuildMessageDeleteEvent) {
                GuildMessageDeleteEvent ev = (GuildMessageDeleteEvent)e;
                try{
                    CachedMessage cache = messageCache.get(ev.getMessageId(), Optional::empty).orElse(null);

                    if(cache == null || cache.getMessage().isEmpty()) {
                        Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                                new EmbedBuilder().setAuthor(cache.getAuthor_name(), null, cache.getAuthor_url())
                                        .setColor(Color.GREEN)
                                        .setDescription("O usuário " + cache.getAuthor_name() + " deletou sua mensagem ```" + cache.getMessage() + "```")
                                        .setFooter("ID: " + cache.getAuthor_id() + " #" + ev.getChannel().getName(), null)); }
                }catch (Exception ignored) {}
            }
            if(e instanceof GuildMessageUpdateEvent) {
                GuildMessageUpdateEvent ev = (GuildMessageUpdateEvent)e;
                try{
                    CachedMessage old_message = messageCache.get(ev.getMessageId(), Optional::empty).orElse(null);

                    if(old_message == null || old_message.getMessage().isEmpty()) {
                        Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                                new EmbedBuilder().setAuthor(ev.getAuthor().getName(), null, ev.getAuthor().getEffectiveAvatarUrl())
                                        .setColor(Color.GREEN)
                                        .setDescription("O usuário " + ev.getAuthor().getName() + " alterou sua mensagem de ```" + old_message.getMessage() + "``` \npara ```" + ev.getMessage().getContent() + "``.")
                                        .setFooter("ID: " + ev.getAuthor().getId() + " #" + ev.getChannel().getName(), null).setTimestamp(ev.getMessage().getEditedTime())); }
                }catch (Exception ignored) {}
            }
            return;
        }
    }

    public static boolean isModuleActive(Guild g, LogModules module) {
        return Main.getDatabase().getGuildProfile(g).isLogModuleActive(module);
    }


    @Getter
    public class CachedMessage {

        String message;
        String author_name;
        String author_url;
        String author_id;

        public CachedMessage(String message, String author_name, String author_url, String author_id) {
            this.message = message; this.author_name = author_name; this.author_url = author_url; this.author_id = author_id;
        }

    }

}
