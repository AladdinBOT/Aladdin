package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.awt.*;
import java.lang.management.ManagementFactory;

/**
 * Created by HeyZeer0 on 21/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class BotCommand implements CommandExecutor {

    @Command(command = "bot", description = "Obtenha informações sobre mim", parameters = {"status/ping/info"}, type = CommandType.INFORMATIVE,
            usage = "a!bot ping\na!bot status\na!bot info")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("ping")) {
            long agora = System.currentTimeMillis();
            e.getChannel().sendTyping().queue(msg -> {
                long novo = System.currentTimeMillis();
                e.sendMessage(EmojiList.PING_PONG + " **Ping:** " + (novo - agora) + "ms :sparkling_heart: " + Math.abs(e.getJDA().getPing()) + "ms");
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("status")) {

            final long
                    duration = ManagementFactory.getRuntimeMXBean().getUptime(),
                    years = duration / 31104000000L,
                    months = duration / 2592000000L % 12,
                    days = duration / 86400000L % 30,
                    hours = duration / 3600000L % 24,
                    minutes = duration / 60000L % 60,
                    seconds = duration / 1000L % 60;
            String uptime = (years == 0 ? "" : years + " Anos, ") + (months == 0 ? "" : months + " Meses, ")
                    + (days == 0 ? "" : days + " Dias, ") + (hours == 0 ? "" : hours + " Horas, ")
                    + (minutes == 0 ? "" : minutes + " Minutos, ") + (seconds == 0 ? "" : seconds + " Segundos, ");

            uptime = replaceLast(uptime, ", ", "");
            uptime = replaceLast(uptime, ",", " e");

            final double ramAllocated = Runtime.getRuntime().totalMemory();
            final double currentMemory = (((double) (Runtime.getRuntime().totalMemory() / 1024) / 1024)) - (((double) (Runtime.getRuntime().freeMemory() / 1024) / 1024));

            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Color.CYAN)
                    .setAuthor("Aladdin - Informações" + "\n\u00ad", null, e.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Para mais informações entre na [guilda do Aladdin](https://discord.gg/ubwxx8A)!")
                    .addField("Uptime: ", uptime, false)
                    .addField(":beginner: Guildas:", "" + e.getJDA().getGuilds().size(), true)
                    .addField(":information_desk_person: Usuários:", "" + e.getJDA().getUsers().size(), true)
                    .addField(":page_with_curl: Canais de texto:", "" + e.getJDA().getTextChannels().size(), true)
                    .addField(":small_blue_diamond: Shards:", Main.getShards().length + "/" + Main.getConnectedShards().length, true)
                    .addField(":desktop: CPU:", getProcessCpuLoad() + "%", true)
                    .addField(":vertical_traffic_light: RAM:", Utils.convertToBar((long)(ramAllocated / 1048576), (long)currentMemory), true)
                    .addField(":musical_note: Canais de voz: ", e.getJDA().getVoiceChannels().size() + "", true)
                    .addBlankField(true)
                    .setFooter("Aladdin - Version " + Main.version, e.getJDA().getSelfUser().getAvatarUrl())

                    .build())
                    .build();

            e.sendMessage(embed);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("info")) {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.CYAN);
            b.setAuthor("Sobre Aladdin", null, "https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
            b.setDescription("Olá meu nome é Aladdin, caso você não saiba eu fui inspirado no anime [Magi](https://pt.wikipedia.org/wiki/Magi_(mang%C3%A1), eu tenho como foco ações administrativas porém posso ser bastante útil em outras coisas variantes desde diversão até musica, sendo desenvolvido pelo ``HeyZeer0#0190``. \nEm minha aventura eu sou acompanhado pelos meus grandes amigos Ali babá e Morgiana, talvez você encontre eles por aí. Meu objetivo é aprender mais e mais sobre este mundo, você pode me chamar para sua guilda clicando [aqui](https://discordapp.com/oauth2/authorize?client_id=321349548712656896&scope=bot&permissions=2146958463)!\nEae, gostaria de ser meu amigo?");
            b.setImage("https://s-media-cache-ak0.pinimg.com/originals/97/a6/4d/97a64d7741a1fe2ad187fa31a5d3e276.jpg");
            b.setFooter("Aladdin v" + Main.version, e.getJDA().getSelfUser().getAvatarUrl());
            e.sendMessage(b);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

    private String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static double getProcessCpuLoad() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try{
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
            if (list.isEmpty())     return Double.NaN;
            Attribute att = (Attribute)list.get(0);
            Double value  = (Double)att.getValue();
            if (value == -1.0)      return Double.NaN;
            return ((int)(value * 1000) / 10.0);
        }catch(Exception ex) {
            return 0.0;
        }
    }

}
