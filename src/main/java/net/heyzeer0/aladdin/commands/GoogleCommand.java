package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.GoogleSearch;
import net.heyzeer0.aladdin.utils.GoogleUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.List;

/**
 * Created by HeyZeer0 on 04/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class GoogleCommand implements CommandExecutor {

    @Command(command = "google", description = "Realize uma pesquisa no google.\n\nObs: você pode definir o valor da pesquisa\nEx: a!google **2** como preparar arroz", aliasses = {"serach", "pesquisar"}, parameters = {"pesquisa"}, type = CommandType.MISCELLANEOUS,
            usage = "a!google como preparar arroz\na!google 2 como preparar arroz")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        String titulo;

        boolean number = false;

        if(!NumberUtils.isCreatable(args.get(0))) {
            titulo = args.getComplete();
        }else{
            number = true;
            if(Integer.valueOf(args.get(0)) <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, o número não pode ser 0 ou menor.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            titulo = args.getCompleteAfter(1);
        }

        e.getChannel().sendTyping().queue();

        List<GoogleSearch> resultados = GoogleUtils.search_google(titulo);

        if(resultados == null || resultados.size() <= 0) {
            e.sendMessage(EmojiList.WORRIED + " Oops, mão foi encontrado um resultado com o item pesquisado.");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        GoogleSearch select = number ? resultados.size() >= (Integer.valueOf(args.get(0)) - 1) ? resultados.get(Integer.valueOf(args.get(0)) - 1) : resultados.get(0) : resultados.get(0);

        EmbedBuilder b = new EmbedBuilder();
        b.setThumbnail("https://www.google.com.br/images/branding/googleg/1x/googleg_standard_color_128dp.png");
        b.setDescription("Resultado da pesquisa por " + titulo + " - Valor: " + (number ? args.get(0) : "1"));

        b.addField(select.getTitle(), select.getUrl(), true);

        b.setFooter("Pesquisa pedida por " + e.getAuthor().getName(), e.getAuthor().getAvatarUrl());
        b.setTimestamp(e.getMessage().getCreationTime());
        b.setColor(Color.GREEN);

        e.getChannel().sendMessage(new MessageBuilder().setEmbed(b.build()).build()).queue();

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
