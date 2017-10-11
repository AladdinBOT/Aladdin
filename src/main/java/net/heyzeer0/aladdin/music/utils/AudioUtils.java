package net.heyzeer0.aladdin.music.utils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.heyzeer0.aladdin.enums.EmojiList;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AudioUtils {

    public static boolean isAlone(VoiceChannel vc) {
        return vc.getMembers().stream().filter(m -> !m.getUser().isBot()).count() == 0;
    }

    public static String format(long length) {
        long hours = length / 3600000L % 24,
                minutes = length / 60000L % 60,
                seconds = length / 1000L % 60;
        return (hours == 0 ? "" : octal(hours) + ":")
                + (minutes == 0 ? "00" : octal(minutes)) + ":" + (seconds == 0 ? "00" : octal(seconds));
    }

    public static String octal(long num) {
        if (num > 9) return String.valueOf(num);
        return "0" + num;
    }

    public static boolean connectChannel(TextChannel tc, Member member) {
        if (tc.getGuild().getAudioManager().isConnected() || tc.getGuild().getAudioManager().isAttemptingToConnect()) {
            while (!tc.getGuild().getAudioManager().isConnected()) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
            }
            if (!tc.getGuild().getAudioManager().getConnectedChannel().equals(member.getVoiceState().getChannel())) {
                tc.sendMessage(EmojiList.WORRIED + " Oops, você não esta conectado ao canal a qual eu estou tocando!").queue();
                return false;
            }
            return true;
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            tc.sendMessage(EmojiList.WORRIED + " Oops, você não esta conectado a um canal de voz!").queue();
            return false;
        }


        VoiceChannel vc = member.getVoiceState().getChannel();
        if (!tc.getGuild().getSelfMember().hasPermission(vc, Permission.VOICE_CONNECT)) {
            tc.sendMessage(EmojiList.WORRIED + " Oops, parece que eu não tenho permisssão para entrar no seu canal de audio!").queue();
            return false;
        } else if (vc.getUserLimit() > 0 && vc.getMembers().size() > vc.getUserLimit() && !tc.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            tc.sendMessage(EmojiList.WORRIED + " Oops, parece que eu não posso entrar no seu canal de audio já que o limite de usuários foi alcançado!").queue();
            return false;
        }
        tc.getGuild().getAudioManager().openAudioConnection(vc);
        return true;

    }

}
