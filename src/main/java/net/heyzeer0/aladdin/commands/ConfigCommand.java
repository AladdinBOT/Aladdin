package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.enums.Lang;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by HeyZeer0 on 06/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ConfigCommand implements CommandExecutor {

    @Command(command = "config", description = "command.config.description", parameters = {"list/set/lang"}, aliasses = {"cfg"}, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!config list\na!config set prefix !!\na!config lang list\na!config lang set PT_BR")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if(args.get(0).equalsIgnoreCase("lang")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "lang", "set/list");
            }

            if(args.get(1).equalsIgnoreCase("list")) {
                Paginator ph = new Paginator(e, lp.get("command.config.lang.list.paginator.title", e.getGuildProfile().getSelectedLanguage().getFlag()));

                for(Lang l : Lang.values()) {
                    ph.addPage(lp.get("command.config.lang.list.paginator.page", l.toString(), l.getAuthor()));
                }

                ph.start();

                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            if(args.get(1).equalsIgnoreCase("set")) {
                if(args.getSize() < 3) {
                    return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "lang", "set", "lang");
                }

                try{
                    Lang l = Lang.valueOf(args.get(2).toUpperCase().replace("-", "_"));

                    e.getGuildProfile().updateLang(l);

                    e.sendMessage(l.getLangProfile().get("command.config.lang.set.success", l.toString()));
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.config.lang.set.error.notfound"));
                }

                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "lang", "set/list");
        }

        if(args.get(0).equalsIgnoreCase("list")) {
            Paginator ph = new Paginator(e, lp.get("command.config.list.paginator.title"));

            for(GuildConfig cfg : GuildConfig.values()) {
                ph.addPage(String.format(lp.get("command.config.list.paginator.page"), cfg.toString().toLowerCase(), lp.get("config." + cfg.toString().toLowerCase() + ".description"), e.getGuildProfile().getConfigValue(cfg)));
            }

            ph.start();

            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        if(args.get(0).equalsIgnoreCase("set")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "set", "nome", "valor");
            }
            String config = args.get(1).toUpperCase().replace(" ", "_");
            try{
                GuildConfig cfg = GuildConfig.valueOf(config);

                try{
                    if(NumberUtils.isCreatable(args.get(2))) {
                        if(!e.getGuildProfile().changeConfig(cfg, Integer.valueOf(args.get(2)))) {
                            throw new Exception();
                        }
                        e.sendMessage(String.format(lp.get("command.config.set.success"), cfg.toString(), args.get(2)));
                        return new CommandResult((CommandResultEnum.SUCCESS));
                    }
                    if(args.get(2).equalsIgnoreCase("true") || args.get(2).equalsIgnoreCase("false")) {
                        if(!e.getGuildProfile().changeConfig(cfg, Boolean.valueOf(args.get(2)))) {
                            throw new Exception();
                        }
                        e.sendMessage(String.format(lp.get("command.config.set.success"), cfg.toString(), args.get(2)));
                        return new CommandResult((CommandResultEnum.SUCCESS));
                    }

                    if(!e.getGuildProfile().changeConfig(cfg, args.getCompleteAfter(2))) {
                        throw new Exception();
                    }
                    e.sendMessage(String.format(lp.get("command.config.set.success"), cfg.toString(), args.getCompleteAfter(2)));

                }catch (Exception ex2) {
                    e.sendMessage(lp.get("command.config.set.error.invalidvalue"));
                }

            }catch (Exception ex) {
                e.sendMessage(lp.get("command.config.set.error.unknownconfig"));
            }
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
