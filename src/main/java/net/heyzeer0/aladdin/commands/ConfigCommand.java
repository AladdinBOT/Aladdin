package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.*;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
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

    @Command(command = "config", description = "Altere as configurações de sua guilda", parameters = {"list/info/set"}, aliasses = {"cfg"}, type = CommandType.ADMINISTRATION, isAllowedToDefault = false,
            usage = "a!config list\na!config info prefix\na!config set prefix !!")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("list")) {
            Paginator ph = new Paginator(e, ":wrench: Configurações da guilda");

            for(GuildConfig cfg : GuildConfig.values()) {
                ph.addPage("Nome: " + cfg.toString().toLowerCase() + "\nDescrição: " + cfg.getDescription() + "\nAtual: " + e.getGuildProfile().getConfigValue(cfg));
            }

            ph.start();

            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        if(args.get(0).equalsIgnoreCase("info")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "info", "nome");
            }
            try{
                GuildConfig cfg = GuildConfig.valueOf(args.get(1).toUpperCase());

                e.sendMessage(EmojiList.CORRECT + " A configuração ``" + cfg.toString() + "`` possui a seguinte descrição ``" + cfg.getDescription() + "``");
            }catch (Exception ex) {
                e.sendMessage(EmojiList.BEGINNER + " O nome inserido é invalido. ");
            }

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
                        e.sendMessage(EmojiList.CORRECT + " Você alterou a configuração ``" + cfg.toString() + "`` para ``" + args.get(2) + "``");
                        return new CommandResult((CommandResultEnum.SUCCESS));
                    }
                    if(args.get(2).equalsIgnoreCase("true") || args.get(2).equalsIgnoreCase("false")) {
                        if(!e.getGuildProfile().changeConfig(cfg, Boolean.valueOf(args.get(2)))) {
                            throw new Exception();
                        }
                        e.sendMessage(EmojiList.CORRECT + " Você alterou a configuração ``" + cfg.toString() + "`` para ``" + args.get(2) + "``");
                        return new CommandResult((CommandResultEnum.SUCCESS));
                    }

                    if(!e.getGuildProfile().changeConfig(cfg, args.getCompleteAfter(2))) {
                        throw new Exception();
                    }
                    e.sendMessage(EmojiList.CORRECT + " Você alterou a configuração ``" + cfg.toString() + "`` para ``" + args.getCompleteAfter(2) + "``");

                }catch (Exception ex2) {
                    e.sendMessage(EmojiList.BEGINNER + " O valor inserido é invalido. ");
                }

            }catch (Exception ex) {
                e.sendMessage(EmojiList.BEGINNER + " O nome inserido é invalido. ");
            }
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
