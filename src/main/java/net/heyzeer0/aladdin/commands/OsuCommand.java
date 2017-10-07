package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.OsuManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.OsuPlayerProfile;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuCommand implements CommandExecutor {

    @Command(command = "osu", description = "Informações sobre Osu!.", parameters = {"perfil", "valor"}, type = CommandType.FUN,
            usage = "a!osu profile HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("perfil")) {
            Utils.runAsync(() -> {
                try{
                    OsuPlayerProfile pf = OsuManager.getUserProfile(args.get(1));

                    if(!pf.isExist()) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o jogador informado não existe.");
                        return;
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.GREEN);
                    b.setAuthor("Osu! Perfil de " + pf.getNome() + " (" + pf.getUserid() + ")", "https://osu.ppy.sh/u/" + pf.getNome(), "https://upload.wikimedia.org/wikipedia/commons/d/d3/Osu%21Logo_%282015%29.png");
                    b.setDescription("Powered by Osu! API");
                    b.addField(":trophy: | Rank:", "**Global:** " + pf.getPp_rank(), true);
                    b.addField("<:empty:363753754874478602>", "**País:** " + pf.getCountry(), true);
                    b.addField("<:empty:363753754874478602>", "**Rank do país:** " + pf.getCountry_rank(), true);
                    b.addField("<:coin:363734535176716288> | Pontos:", "**Vezes jogadas:** " + pf.getPlaycount(), true);
                    b.addField("<:empty:363753754874478602>", "**Pontos feitos:** " + pf.getTotal_score(), true);
                    b.addField("<:empty:363753754874478602>", "**Vezes jogadas:** " + pf.getPlaycount(), true);
                    b.addField("<:target:363735007874777088> | Acertos:", "**Pontos 300:** " + pf.getCount300(), true);
                    b.addField("<:empty:363753754874478602>", "**Pontos 100:** " + pf.getCount100(), true);
                    b.addField("<:empty:363753754874478602>", "**Pontos 50:** " + pf.getCount50(), true);
                    b.addField(":musical_note: | Pontos Musicais:", "**Músicas SS:** " + pf.getCount_rank_ss(), true);
                    b.addField("<:empty:363753754874478602>", "**Músicas S:** " + pf.getCount_rank_s(), true);
                    b.addField("<:empty:363753754874478602>", "**Músicas A:** " + pf.getCount_rank_a(), true);
                    b.addField(":pen_ballpoint: | Perfil:", "**Precisão:** " + Math.round(Double.valueOf(pf.getAccuracy())) + "%", true);
                    b.addField("<:empty:363753754874478602>", "**Level:** " + Math.round(Double.valueOf(pf.getLevel())), true);
                    b.addField("<:empty:363753754874478602>",  "**PP:** " + pf.getPp_raw() + "\u00ad", true);
                    b.setTimestamp(e.getMessage().getCreationTime());
                    b.setFooter("Perfil pedido por " + e.getAuthor().getName(), e.getAuthor().getAvatarUrl());

                    e.sendMessage(b);

                }catch(Exception ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, ocorreu um erro ao tentar adquirir os dados do jogador informado: ``" + ex.getMessage() + "``");
                }
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
