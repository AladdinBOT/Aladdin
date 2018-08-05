package net.heyzeer0.aladdin.events.listeners;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class VoiceListener {

    public static void onVoiceLeave(VoiceChannel vc) {
        GuildTrackProfile scheduler = Main.getMusicManger().getManager (vc.getGuild());
        if (scheduler.scheduleLeave()) {
            TextChannel tc = scheduler.getCurrentTrack().getChannel();
            if (tc != null && tc.canTalk()) {
                tc.sendMessage(":musical_note: Todos sairam do canal, irei finalizar a playlist em 1 minuto.").queue();
            }
        }
    }

    public static void onVoiceJoin(VoiceChannel vc) {
        GuildTrackProfile scheduler = Main.getMusicManger().getManager(vc.getGuild());
        if (scheduler.cancelLeave()) {
            TextChannel tc = scheduler.getCurrentTrack().getChannel();
            if (tc != null && tc.canTalk()) {
                tc.sendMessage(":musical_note: Alguem entrou no canal, finalizando contagem.").queue();
            }
        }
    }

}
