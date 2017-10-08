package net.heyzeer0.aladdin.manager.custom;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.profiles.custom.CrashProfile;
import net.heyzeer0.aladdin.utils.Utils;

import java.io.File;

/**
 * Created by HeyZeer0 on 08/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CrashManager {

    public static boolean verifyCrash(GuildMessageReceivedEvent e) {
        if(!e.getMessage().getAttachments().isEmpty()) {
            Message.Attachment a = e.getMessage().getAttachments().get(0);
            if(a.getFileName().contains("crash") && a.getFileName().contains("-client.txt")) {
                GuildProfile gd = Main.getDatabase().getGuildProfile(e.getGuild());
                if(Boolean.valueOf(gd.getConfigValue(GuildConfig.MINECRAFT_CRASHHELPER).toString())) {
                    Utils.runAsync(() -> {
                        File folder = new File(Main.getDataFolder(), "crash-helper");
                        if(!folder.exists()) {
                            folder.mkdir();
                        }
                        File file = new File(folder, "crash-" + Utils.r.nextInt(Integer.MAX_VALUE) + "-" + e.getAuthor().getName());
                        try{
                            a.download(file);
                        }catch (Exception ex){

                        }finally {
                            e.getChannel().sendMessage(new CrashProfile(file).applyEmbed(e)).queue();
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

}
