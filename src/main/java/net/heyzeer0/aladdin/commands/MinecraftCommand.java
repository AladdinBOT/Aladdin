package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.MojangProfile;
import net.heyzeer0.aladdin.utils.Router;

import java.awt.*;

/**
 * Created by HeyZeer0 on 25/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MinecraftCommand implements CommandExecutor {

    @Command(command = "minecraft", description = "command.minecraft.description", aliasses = {"mc"}, parameters = {"status/skin"}, type = CommandType.FUN,
            usage = "a!minecraft status\na!minecraft skin HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("status")) {
            new MojangProfile().sendAsEmbed(e, lp);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("skin")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "skin", "nick");
            }


            try{
                String uuid = new Router("https://api.mojang.com/users/profiles/minecraft/" + args.get(1)).getResponse().asJsonObject().getString("id");

                e.sendMessage(new EmbedBuilder()
                        .setTitle(String.format(lp.get("command.minecraft.skin.embed.title"), args.get(1)))
                        .setImage("https://crafatar.com/renders/body/uuid/" + uuid)
                        .setColor(Color.GREEN));
            }catch (Exception ex) {
                e.sendMessage(lp.get("command.minecraft.skin.error"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
