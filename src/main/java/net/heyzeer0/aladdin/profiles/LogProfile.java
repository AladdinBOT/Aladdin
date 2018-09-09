/*
 * Developed by HeyZeer0 on 09/09/18 10:23.
 * Last Modification 09/09/18 10:19.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.profiles;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.enums.ConsoleColors;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogProfile {

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static long lastCheck = 0l;

    String name;

    public LogProfile(String name) {
        this.name = name;
    }

    public void warn(String msg) {
        System.out.println(ConsoleColors.RED + "[" + dateFormat.format(new Date()) + "] [" + name + "/WARN] " + msg + ConsoleColors.RESET);
    }

    public void info(String msg) {
        System.out.println(ConsoleColors.CYAN + "[" + dateFormat.format(new Date()) + "] [" + name + "/INFO] " + msg + ConsoleColors.RESET);
    }

    public void alert(String msg) {
        System.out.println(ConsoleColors.YELLOW + "[" + dateFormat.format(new Date()) + "] [" + name + "/ALERT] " + msg + ConsoleColors.RESET);
    }

    public void startMsCount() {
        lastCheck = System.currentTimeMillis();
    }

    public void finishMsCount(String name) {
        info("Took " + (System.currentTimeMillis() - lastCheck) + "ms to load " + name);
    }

    public void embed(String title, String description, Color color) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(color);
        b.setTitle(title);
        b.setDescription(description);
        b.setFooter("Ocorrido as " + dateFormat.format(new Date()), Main.getShards()[0].getJDA().getSelfUser().getEffectiveAvatarUrl());

        Main.getTextChannelById(BotConfig.bot_guildlog_id).sendMessage(b.build()).queue();
    }

    public void exception(Exception ex) {
        EmbedBuilder b = new EmbedBuilder();
        b.setTitle("New exception caught");
        b.setDescription("```fix\n" + Utils.getStackTrace(ex) + "```");
        b.setFooter("Ocorrido as " + dateFormat.format(new Date()), Main.getShards()[0].getJDA().getSelfUser().getEffectiveAvatarUrl());
        b.setColor(Color.GREEN);

        Main.getTextChannelById(BotConfig.bot_guildlog_id).sendMessage("<@" + BotConfig.bot_owner + ">").queue();
        Main.getTextChannelById(BotConfig.bot_guildlog_id).sendMessage(b.build()).queue();
    }

}
