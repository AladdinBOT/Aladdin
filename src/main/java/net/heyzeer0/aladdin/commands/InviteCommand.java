package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;

/**
 * Created by HeyZeer0 on 07/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class InviteCommand implements CommandExecutor {

    @Command(command = "invite", description = "command.invite.description", type = CommandType.INFORMATIVE,
            usage = "a!invite", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setAuthor(lp.get("command.bot.info.embed.author"), null, "https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
        b.setDescription(lp.get("command.bot.info.embed.description"));
        b.setImage("http://dl.heyzeer0.tk/Aladdin/aladdin_friend.png");
        b.setFooter("Aladdin v" + Main.version, e.getJDA().getSelfUser().getAvatarUrl());
        e.sendMessage(b);
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
