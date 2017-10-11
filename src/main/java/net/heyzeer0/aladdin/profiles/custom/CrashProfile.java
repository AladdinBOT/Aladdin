package net.heyzeer0.aladdin.profiles.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.MainConfig;
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

    String auto_solve = "Ainda não há uma resolução automatica para este crash, tomei a liberade de enviar ele ao meu autor.";

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

        if(finallines.toLowerCase().contains("pixel format not accelerated")) {
            auto_solve = "Os drivers da sua placa de vídeo podem estar desatualizados, atualize-os e tente novamente, caso não funcione sua placa de vídeo não possui suporte ao OpenGL requisitado.";
        }
        if(finallines.toLowerCase().contains("already tesselating!")) {
            auto_solve = "Este erro ocorre quando algo sobrepoem a renderização de outra coisa, normalmente não é necessário fazer nada apenas reabrir seu jogo. Caso o problema persista contate moderadores.";
        }
        if(finallines.toLowerCase().contains("could not get provider type for dimension")) {
            auto_solve = "Você encontra-se em uma dimensão invalida, para resolver peça a um administrador para te mover ao spawn.";
        }
        if(finallines.toLowerCase().contains("outofmemoryerror")) {
            auto_solve = "Seu jogo possui pouca memória alocada (" + memory + "), aumente essa quantidade e seu problema sera resolvido.";
        }
        if(finallines.toLowerCase().contains("kihira.foxlib.client.TextureHelper$.getPlayerSkinAsBufferedImage".toLowerCase())) {
            auto_solve = "Este crash ainda não possui nenhuma solução viável, é causado pelo foxlib e suas falhas são relacionadas a tentar pegar a skin do jogador, talvez relacionado a jogadores piratas.";
        }
        if(finallines.toLowerCase().contains("IndexOutOfBoundsException".toLowerCase())) {
            auto_solve = "Este crash esta relacionado a cliques em slots invalidos, existem diversas causas geralmente resolvido só de abrir o jogo novamente.";
        }


        if(finallines != null) {
            try{
                hastebin_url = Utils.sendToHastebin(finallines);

            }catch (Exception e) {}
        }
    }

    public Message applyEmbed(GuildMessageReceivedEvent e) {

        if(auto_solve.contains("tomei a liberade de enviar ele ao meu autor")) {
            e.getJDA().getUserById(MainConfig.bot_owner).openPrivateChannel().queue(pv -> pv.sendMessage("Crash Solver não presente " + hastebin_url).queue());
        }

        EmbedBuilder b = new EmbedBuilder();

        b.setTitle("Crash Report Utilities", null);
        b.setDescription("Inspeção iniciada por ``" + e.getAuthor().getName() + "``");
        b.setThumbnail(e.getAuthor().getAvatarUrl());
        b.setColor(Color.GREEN);

        b.addField("Versão do Minecraft", minecraft_version, true);
        b.addField("Memória (U/L/M)", memory, true);
        b.addField("Solução automatica", auto_solve, false);
        b.addField("Versão do Java", java_version, true);
        b.addField("Hastebin", hastebin_url, true);

        b.setFooter("Aladdin v" + Main.version, e.getJDA().getSelfUser().getAvatarUrl());

        return new MessageBuilder().setEmbed(b.build()).build();
    }

}
