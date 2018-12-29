package net.heyzeer0.aladdin.profiles.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 19/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CrashProfile {

    private static Pattern memory_pattern = Pattern.compile("(\\(.*?\\))");

    String minecraft_version = "";
    String memory = "";
    String java_version = "";
    File file;

    String finallines;
    String hastebin_url = "Ocorreu um erro ao enviar para o hastebin.";

    public CrashProfile(File local_file) {
        this.file = local_file;

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                if(line.toLowerCase().contains("minecraft version:")) {
                    minecraft_version = line.replace("\tMinecraft Version: ", "");
                }
                if(line.toLowerCase().contains("java version:")) {
                    java_version = line.replace("\tJava Version: ", "");
                }

                if(line.toLowerCase().contains("memory:")) {
                    String fkMemory = line.replace("\tMemory: ", "");
                    Matcher matcher = memory_pattern.matcher(fkMemory);
                    while (matcher.find()) {
                        if(memory == "") {
                            memory = matcher.group().replace("(", "").replace(")", "");
                            fkMemory = fkMemory.replace(matcher.group(), "");
                            continue;
                        }
                        memory = memory + " | " + matcher.group().replace("(", "").replace(")", "");

                        fkMemory = fkMemory.replace(matcher.group(), "");
                    }
                }
                finallines = finallines + "\n" + line;
            }
        }catch (Exception e) {}finally {file.delete();}



        if(finallines != null) {
            try{
                hastebin_url = Utils.sendToHastebin(finallines);

            }catch (Exception e) {}
        }
    }

    public Message applyEmbed(GuildMessageReceivedEvent e) {

        EmbedBuilder b = new EmbedBuilder();

        b.setTitle("Crash Report Utilities", null);
        b.setDescription("Started by ``" + e.getAuthor().getName() + "``");
        b.setThumbnail(e.getAuthor().getAvatarUrl());
        b.setColor(Color.GREEN);

        b.addField("Minecraft Version", minecraft_version, true);
        b.addField("Memory (U/F/M)", memory, true);
        b.addField("Java Version", java_version, true);
        b.addField("Hastebin", hastebin_url, true);

        b.setFooter("Aladdin v" + Main.version, e.getJDA().getSelfUser().getAvatarUrl());

        return new MessageBuilder().setEmbed(b.build()).build();
    }

}
