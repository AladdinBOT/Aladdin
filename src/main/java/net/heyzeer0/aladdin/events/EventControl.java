package net.heyzeer0.aladdin.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.heyzeer0.aladdin.events.listeners.GuildListener;
import net.heyzeer0.aladdin.events.listeners.MessageListener;
import net.heyzeer0.aladdin.events.listeners.VoiceListener;
import net.heyzeer0.aladdin.music.utils.AudioUtils;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class EventControl implements EventListener {

    @Override
    public void onEvent(Event e) {
        if(e instanceof GuildMessageReceivedEvent) {
            MessageListener.onMessage((GuildMessageReceivedEvent)e);
            return;
        }
        if(e instanceof MessageReactionAddEvent) {
            MessageListener.onReact((MessageReactionAddEvent)e);
            return;
        }
        if(e instanceof GuildJoinEvent) {
            GuildListener.onGuildJoin((GuildJoinEvent)e);
            return;
        }
        if(e instanceof GuildLeaveEvent) {
            GuildListener.onGuildLeave((GuildLeaveEvent)e);
            return;
        }
        if(e instanceof GuildMemberLeaveEvent) {
            GuildListener.onMemberLeave((GuildMemberLeaveEvent)e);
            return;
        }
        if(e instanceof GuildVoiceJoinEvent) {
            GuildVoiceJoinEvent event = (GuildVoiceJoinEvent) e;
            if(!event.getMember().getUser().isBot() && event.getChannelJoined().getMembers().contains(event.getGuild().getSelfMember())) {
                VoiceListener.onVoiceJoin(((GuildVoiceJoinEvent)e).getChannelJoined());
            }
        }
        if(e instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent event = (GuildVoiceLeaveEvent) e;
            if(event.getChannelLeft().getMembers().contains(event.getGuild().getSelfMember()) && AudioUtils.isAlone(event.getChannelLeft())) {
                VoiceListener.onVoiceLeave(((GuildVoiceLeaveEvent)e).getChannelLeft());
            }

        }
        if(e instanceof GuildVoiceMoveEvent) {
            GuildVoiceMoveEvent event = (GuildVoiceMoveEvent) e;
            if(event.getChannelJoined().getMembers().contains(event.getGuild().getSelfMember()) && AudioUtils.isAlone(event.getChannelJoined())) {
                VoiceListener.onVoiceJoin(event.getChannelJoined());
            }

            if(event.getChannelLeft().getMembers().contains(event.getGuild().getSelfMember()) && AudioUtils.isAlone(event.getChannelLeft())) {
                VoiceListener.onVoiceLeave(event.getChannelLeft());
            }
        }
    }

}
