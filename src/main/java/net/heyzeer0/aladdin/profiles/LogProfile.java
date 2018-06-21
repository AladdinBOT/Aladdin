package net.heyzeer0.aladdin.profiles;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.enums.ConsoleColors;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HeyZeer0 on 05/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
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

}
