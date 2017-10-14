package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AdminCommand implements CommandExecutor {

    @Command(command = "admin", description = "Comandos sobre o bot", type = CommandType.BOT_ADMIN, isAllowedToDefault = false,
            usage = "")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("key")) {
            for(User u : e.getMessage().getMentionedUsers()) {
                Main.getDatabase().getUserProfile(u).addKeys(2);
            }
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
