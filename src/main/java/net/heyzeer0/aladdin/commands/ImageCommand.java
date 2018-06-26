package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.PixaBayManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
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

    @Command(command = "image", description = "command.image.description", parameters = {"tag"}, type = CommandType.MISCELLANEOUS,
            usage = "a!image carro")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        Utils.runAsync(() -> {

            try{
                List<PixaBayProfile> pbp = PixaBayManager.getImages(args.getComplete().replace(" ", "+"));

                if(pbp == null) {
                    e.sendMessage(String.format(lp.get("command.image.notfound"), args.getComplete()));
                    return;
                }

                PixaBayProfile pf = pbp.get(Utils.r.nextInt(pbp.size()));

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setTitle(String.format(lp.get("command.image.embed.title"), args.getComplete()));
                b.setDescription(String.format(lp.get("command.image.embed.description"), pf.getPageURL()));
                b.addField(lp.get("command.image.embed.field.1"), pf.getDownloads() + "", true);
                b.addField(lp.get("command.image.embed.field.2"), pf.getLikes() + "", true);
                b.setImage(pf.getWebformatURL());
                b.setFooter(String.format(lp.get("command.image.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);
            }catch (Exception ex) {
                e.sendMessage(String.format(lp.get("command.image.error"), ex.getMessage()));
                ex.printStackTrace();
            }

        });

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
