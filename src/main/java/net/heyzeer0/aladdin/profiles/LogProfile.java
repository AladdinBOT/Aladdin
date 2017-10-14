package net.heyzeer0.aladdin.profiles;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.MainConfig;

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

    String name;

    public LogProfile(String name) {
        this.name = name;
    }

    public void warn(String msg) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [" + name + "/WARN] " + msg);
    }

    public void info(String msg) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [" + name + "/INFO] " + msg);
    }

    public void embed(String title, String description, Color color) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(color);
        b.setTitle(title);
        b.setDescription(description);
        b.setFooter("Ocorrido as " + dateFormat.format(new Date()), Main.getShards()[0].getJDA().getSelfUser().getEffectiveAvatarUrl());

        Main.getTextChannelById(MainConfig.bot_guildlog_id).sendMessage(b.build()).queue();
    }

}
