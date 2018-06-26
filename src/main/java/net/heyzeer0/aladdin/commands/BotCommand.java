package net.heyzeer0.aladdin.commands;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
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

    @Command(command = "bot", description = "command.bot.description", parameters = {"status/ping/info"}, type = CommandType.INFORMATIVE,
            usage = "a!bot ping\na!bot status\na!bot info", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
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
            String uptime = (years == 0 ? "" : years + " " + lp.get("command.bot.status.years") + ", ") + (months == 0 ? "" : months + " " + lp.get("command.bot.status.months") + ", ")
                    + (days == 0 ? "" : days + " " + lp.get("command.bot.status.days") + ", ") + (hours == 0 ? "" : hours + " " + lp.get("command.bot.status.hours") + ", ")
                    + (minutes == 0 ? "" : minutes + " " + lp.get("command.bot.status.minutes") + ", ") + (seconds == 0 ? "" : seconds + " " + lp.get("command.bot.status.seconds") + ", ");

            uptime = replaceLast(uptime, ", ", "");
            uptime = replaceLast(uptime, ",", " e");

            final double ramAllocated = Runtime.getRuntime().totalMemory();
            final double currentMemory = (((double) (Runtime.getRuntime().totalMemory() / 1024) / 1024)) - (((double) (Runtime.getRuntime().freeMemory() / 1024) / 1024));

            Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(lp.get("command.bot.status.embed.title"), null, e.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(String.format(lp.get("command.bot.status.embed.description"), JDAInfo.VERSION))
                    .addField(lp.get("command.bot.status.embed.field.1"), uptime, false)
                    .addField(lp.get("command.bot.status.embed.field.2"), "" + e.getJDA().getGuilds().size(), true)
                    .addField(lp.get("command.bot.status.embed.field.3"), "" + e.getJDA().getUsers().size(), true)
                    .addField(lp.get("command.bot.status.embed.field.4"), "" + e.getJDA().getTextChannels().size(), true)
                    .addField(lp.get("command.bot.status.embed.field.5"), Main.getShards().length + "/" + Main.getConnectedShards().length, true)
                    .addField(lp.get("command.bot.status.embed.field.6"), getProcessCpuLoad() + "%", true)
                    .addField(lp.get("command.bot.status.embed.field.7"), Utils.convertToBar((long)(ramAllocated / 1048576), (long)currentMemory), true)
                    .addField(lp.get("command.bot.status.embed.field.8"), e.getJDA().getVoiceChannels().size() + "", true)
                    .addField(lp.get("command.bot.status.embed.field.9"), MusicManager.getManagers().size() + "", true)
                    .addField(lp.get("command.bot.status.embed.field.10"), PlayerLibrary.VERSION, true)
                    .setFooter("Aladdin - Version " + Main.version, e.getJDA().getSelfUser().getAvatarUrl())

                    .build())
                    .build();

            e.sendMessage(embed);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("info")) {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setAuthor(lp.get("command.bot.info.embed.author"), null, "https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
            b.setDescription(lp.get("command.bot.info.embed.description"));
            b.setImage("http://dl.heyzeer0.tk/Aladdin/aladdin_friend.png");
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
