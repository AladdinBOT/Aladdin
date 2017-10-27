package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.*;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;

/**
 * Created by HeyZeer0 on 27/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class LogCommand implements CommandExecutor {

    @Command(command = "log", description = "Configure o log da sua guilda", parameters = {"setchannel/modulos"}, type = CommandType.ADMINISTRATION,
            usage = "a!log setchannel #log\na!log setchannel\na!log modulos ativar MESSAGE_MODULE\na!log modulos desativar MESSAGE_MODULE\na!log modulos list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("setchannel")) {
            if(e.getMessage().getMentionedChannels().size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não mencionou nenhum canal.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().changeLogChannel(e.getMessage().getMentionedChannels().get(0).getId());

            e.sendMessage(EmojiList.CORRECT + " Você alterou o canal log da guilda para ``" + e.getMessage().getMentionedChannels().get(0).getName() + "``");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("modulos")) {

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modulos", "ativar/desativar/list");
            }

            if(args.get(1).equalsIgnoreCase("list")) {
                Paginator ph = new Paginator(e, ":wrench: Lista de todos os modulos");

                for(LogModules cfg : LogModules.values()) {
                    ph.addPage("Nome: " + cfg.toString().toLowerCase() + "\nDescrição: " + cfg.getDescription() + "\nAtivo: " + (e.getGuildProfile().isLogModuleActive(cfg) ? "Sim" : "Não"));
                }

                ph.start();

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).equalsIgnoreCase("ativar")) {

                if(args.getSize() < 3) {
                    return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modulos ativar", "nome");
                }

                try{
                    LogModules md = LogModules.valueOf(args.get(2).toUpperCase());

                    e.getGuildProfile().changeLogModuleStatus(md, true);

                    e.sendMessage(EmojiList.CORRECT + " Você ativou o modulo ``" + md.toString() + "``");
                }catch (Exception ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o modulo indicado é invalido.");
                }

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).equalsIgnoreCase("desativar")) {

                if(args.getSize() < 3) {
                    return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modulos desativar", "nome");
                }

                try{
                    LogModules md = LogModules.valueOf(args.get(2).toUpperCase());

                    e.getGuildProfile().changeLogModuleStatus(md, false);

                    e.sendMessage(EmojiList.CORRECT + " Você desativou o modulo ``" + md.toString() + "``");
                }catch (Exception ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o modulo indicado é invalido.");
                }

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "modulos", "ativar/desativar/list");
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
