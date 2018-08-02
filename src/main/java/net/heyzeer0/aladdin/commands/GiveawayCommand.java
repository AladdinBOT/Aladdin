package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.GiveawayManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.builders.GiveawayBuilder;

/**
 * Created by HeyZeer0 on 14/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GiveawayCommand implements CommandExecutor {

    @Command(command = "giveaway", description = "Faça sorteios automaticos!", parameters = {"create", "formula"}, extra_perm = {"takewinner"}, type = CommandType.MISCELLANEOUS,
            usage = "a!giveaway create", isAllowedToDefault = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("create")) {
            if(GiveawayManager.giveways.containsKey(e.getAuthor().getId())) {
                e.sendMessage(lp.get("command.giveaway.create.alreadybuilding"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            new GiveawayBuilder(e, lp);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("formula")) {
            if(args.getSize() < 2) {
                e.sendMessage(lp.get("command.giveaway.formula.success.1", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "giveaway formula [seed]"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }


            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }

}
