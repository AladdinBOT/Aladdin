package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.PixaBayManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.PixaBayProfile;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.util.List;

/**
 * Created by HeyZeer0 on 16/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ImageCommand implements CommandExecutor {

    @Command(command = "image", description = "Obtenha uma imagem com a tag selecionada", parameters = {"tag"}, type = CommandType.MISCELLANEOUS,
            usage = "a!image carro")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        Utils.runAsync(() -> {

            try{
                List<PixaBayProfile> pbp = PixaBayManager.getImages(args.getComplete().replace(" ", "+"));

                if(pbp == null) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, não há resultados para ``" + args.getComplete() + "``");
                    return;
                }

                PixaBayProfile pf = pbp.get(Utils.r.nextInt(pbp.size()));

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setDescription("Powered by PixaBay, clique [aqui](" + pf.getPageURL() + ") para ir até a página, sem NSFW");
                b.setTitle(":frame_photo: | Imagem para " + args.getComplete());
                b.addField(EmojiList.BUY + " Downloads", pf.getDownloads() + "", true);
                b.addField(EmojiList.HEART + " Likes", pf.getLikes() + "", true);
                b.setImage(pf.getWebformatURL());
                b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);
            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, ocorreu um erro ao executar este comando ``" + ex.getMessage() + "``");
                ex.printStackTrace();
            }

        });

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
