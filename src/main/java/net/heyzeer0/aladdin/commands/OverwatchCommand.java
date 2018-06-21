package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.OverwatchManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.OverwatchPlayer;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class OverwatchCommand implements CommandExecutor{

    @Command(command = "overwatch", description = "Comandos sobre overwatch.", aliasses = {"ow"}, parameters = {"profile", "user#id"}, type = CommandType.FUN,
            usage = "a!overwatch perfil HeyZeer0#1903")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("profile")) {

            String user = args.get(1).replace("#", "-");

            if(!user.contains("-")) {
                e.sendMessage(EmojiList.WORRIED + " Oops, é necessário incluir a id do jogador, exemplo: ``HeyZeer0#1903``");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            Utils.runAsync(() -> {
                try{
                    OverwatchPlayer pf = OverwatchManager.getUserProfile(user);

                    if(pf == null) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o jogador ``" + user + "`` não existe.");
                        return;
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.GREEN);
                    b.setAuthor("Perfil de " + pf.getUsername(), null, "http://i.imgur.com/YZ4w2ey.png");
                    b.setDescription("Powered by [Overwatch-api](https://github.com/alfg/overwatch-api)");
                    b.setThumbnail(pf.getPortrait());
                    b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
                    b.setTimestamp(e.getMessage().getCreationTime());

                    b.addField(":pen_ballpoint: | Perfil:", "**Nível:** " + pf.getLevel(), true);
                    b.addField("<:empty:363753754874478602>", "**Rank:** " + (pf.getCompetitiveRank() != null ? pf.getCompetitiveRank() : "Unranked"), true);
                    b.addField(":trophy: | Ranqueada:", "**Vitórias:** " + (pf.getCompetitiveWins() != null ? pf.getCompetitiveWins() : "0"), true);
                    b.addField("<:empty:363753754874478602>", "**Derrotas:** " + (pf.getCompetitiveLosts() != null ? pf.getCompetitiveLosts() : "0"), true);
                    b.addField(":video_game: | Quickplay:", "**Vitórias:** " + (pf.getQuickplayWins() != null ? pf.getQuickplayWins() : "0"), true);
                    b.addBlankField(true);
                    b.addField(":clock1: | Tempo de jogo:", "**Competitivo:** " + (pf.getCompetitiveTime() != null ? pf.getCompetitiveTime() : "0"), true);
                    b.addField("<:empty:363753754874478602>", "**Quickplay:** " + (pf.getQuickplayTime() != null ? pf.getQuickplayTime() : "0"), true);

                    e.sendMessage(b);

                }catch (Exception ex) { e.sendMessage(EmojiList.WORRIED + " Oops, parece que ocorreu um erro ao requerir os dados ``" + ex.getMessage() + "``");}
            });

            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }


}
