/*
 * Developed by HeyZeer0 on 11/20/18 1:09 PM.
 * Last Modification 11/20/18 1:08 PM.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

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

public class GiveawayCommand implements CommandExecutor {

    @Command(command = "giveaway", description = "Faça sorteios automaticos!", parameters = {"create/formula"}, extra_perm = {"takewinner"}, type = CommandType.MISCELLANEOUS,
            usage = "a!giveaway create", isAllowedToDefault = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("create")) {
            if(GiveawayManager.giveaways.containsKey(e.getAuthor().getId())) {
                e.sendMessage(lp.get("command.giveaway.create.alreadybuilding"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            new GiveawayBuilder(e, lp);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("formula")) {
            if(args.getSize() < 3) {
                e.sendMessage(lp.get("command.giveaway.formula.success.1", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "giveaway formula [seed] [participants]"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            try{
                int seed = Integer.valueOf(args.get(1));
                int participants = Integer.valueOf(args.get(2));

                e.sendMessage(lp.get("command.giveaway.formula.success.2", (int)(seed % participants)));
            }catch (Exception ex) {
                e.sendMessage(lp.get("command.giveaway.formula.error.invalidnumber"));
            }

            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }

}
