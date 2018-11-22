/*
 * Developed by HeyZeer0 on 11/22/18 8:05 PM.
 * Last Modification 11/22/18 8:05 PM.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.LogModules;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;

public class LogCommand implements CommandExecutor {

    @Command(command = "log", description = "command.log.description", parameters = {"setchannel/modules"}, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!log setchannel #log\na!log setchannel\na!log modulos activate MESSAGE_MODULE\na!log modulos deactivate MESSAGE_MODULE\na!log modules list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("setchannel")) {
            if(e.getMessage().getMentionedChannels().size() <= 0) {
                e.sendMessage(lp.get("command.log.setchannel.error"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().changeLogChannel(e.getMessage().getMentionedChannels().get(0).getId());

            e.sendMessage(String.format(lp.get("command.log.setchannel.success"), e.getMessage().getMentionedChannels().get(0).getName()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("modules")) {

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modules", "activate/deactivate/list");
            }

            if(args.get(1).equalsIgnoreCase("list")) {
                Paginator ph = new Paginator(e, lp.get("command.log.modules.list.paginator.title"));

                for(LogModules cfg : LogModules.values()) {
                    ph.addPage(String.format(lp.get("command.log.modules.list.paginator.page"), cfg.toString().toLowerCase(), cfg.getDescription(), e.getGuildProfile().isLogModuleActive(cfg)));
                }

                ph.start();

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).equalsIgnoreCase("activate")) {

                if(args.getSize() < 3) {
                    return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modules activate", "nome");
                }

                try{
                    LogModules md = LogModules.valueOf(args.get(2).toUpperCase());

                    e.getGuildProfile().changeLogModuleStatus(md, true);

                    e.sendMessage(String.format(lp.get("command.log.modules.activate.success"), md.toString()));
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.log.modules.error"));
                }

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).equalsIgnoreCase("deactivate")) {

                if(args.getSize() < 3) {
                    return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modules deactivate", "nome");
                }

                try{
                    LogModules md = LogModules.valueOf(args.get(2).toUpperCase());

                    e.getGuildProfile().changeLogModuleStatus(md, false);

                    e.sendMessage(String.format(lp.get("command.log.modules.deactivate.success"), md.toString()));
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.log.modules.error"));
                }

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modules", "activate/deactivate/list");
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
