package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.GoogleUtils;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 16/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ImageCommand implements CommandExecutor {

    @Command(command = "imagem", description = "Obtenha uma imagem com a tag selecionada", parameters = {"tag"}, type = CommandType.MISCELLANEOUS,
            usage = "a!imagem carro")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        Utils.runAsync(() -> {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setTitle(":frame_photo: | Imagem para " + args.getComplete());
            b.setImage(GoogleUtils.search_image(args.getComplete().replace(" ", "%20")));
            b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());


            e.sendMessage(b);
        });

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
