package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.AladdinData;
import net.heyzeer0.aladdin.enums.*;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.CustomCommand;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import net.heyzeer0.aladdin.profiles.utilities.chooser.TextChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by HeyZeer0 on 21/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CommandsCommand implements CommandExecutor {

    @Command(command = "commands", description = "Crie comandos customizaveis", extra_perm = {"overpass"}, aliasses = {"cmds"}, parameters = {"create/delete/listargs/list/import/raw"}, type = CommandType.ADMINISTRATION,
            usage = "a!commands listargs\na!commands list\na!commands create ata Você é #random[feio,bonito]\na!commands import ata\na!commands delete ata\na!commands raw ata")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("listargs")) {
            Paginator ph = new Paginator(e, ":wrench: Argumentos para comandos");
            ph.addPage("Argumento: #arg\nUso: Requerir um argumento\nExemplo: #arg foi assasinado por #arg");
            ph.addPage("Argumento: #regex_arg[regex]\nUso: Requerir um argumento dentro do regex\nExemplo: Você perdeu #regex_arg[(o jogo)]");
            ph.addPage("Argumento: #random[obj,obj...]\nUso: Aleatorizar entre palavras escolhidas\nExemplo: Você #random[morreu,perdeu o jogo,explodiu]");
            ph.addPage("Argumento: #random_int[valor]\nUso: Aleatoriza um numero pelo valor maximo escolhido\nExemplo: O número sorteado foi #random_int[30]");
            ph.addPage("Argumento: #user\nUso: Altera para o nome do usuário\nExemplo: Tudo bom #user ?");
            ph.addPage("Argumento: #mention\nUso: Menciona o usuário\nExemplo: Tudo bom #mention ?");
            ph.addPage("Argumento: -delete\nUso: Deleta a mensagem do usuário após uso\nExemplo ¯\\_(ツ)_/¯ -delete");

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {
            Paginator ph = new Paginator(e, EmojiList.BEGINNER + " Listando comandos");

            if(e.getGuildProfile().getCommands().size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Ops, parece que não existem comandos customizados nessa guilda ^0^");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            int pages = (e.getGuildProfile().getCommands().size() + (10 + 1)) / 10;

            Set<Map.Entry<String, CustomCommand>> cmdsEntry = e.getGuildProfile().getCommands().entrySet();
            ArrayList<Map.Entry<String, CustomCommand>> list = new ArrayList<>(cmdsEntry);

            Integer actual = 0;
            Integer pactual = 1;


            for (int i = 1; i <= pages; i++) {
                String pg = "";
                for (int p = actual; p < pactual * 10; p++) {
                    if (list.size() <= p) {
                        break;
                    }
                    actual++;
                    String user = e.getJDA().getUserById(list.get(p).getValue().getCreator()) == null ? list.get(p).getValue().getCreator() : e.getJDA().getUserById(list.get(p).getValue().getCreator()).getName();
                    pg = pg + "Nome: " + list.get(p).getKey() + " | Autor: " + user + "\n";
                }
                pactual++;
                ph.addPage(pg);
            }

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("create")) {
            if(!Boolean.valueOf(e.getGuildProfile().getConfigValue(GuildConfig.MEMBER_CREATE_CMDS).toString())) {
                if(!e.hasPermission("command.commands.overpass")) {
                    return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
                }
            }
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome");
            }
            String msg = args.getCompleteAfter(2);
            if (e.getGuildProfile().hasCustomCommand(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Ops, parece que este comando já existe ^0^");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(EmojiList.CORRECT + " O comando ``" + args.get(1).toLowerCase() + "`` foi criado com sucesso.");
            e.getGuildProfile().createCustomCommand(args.get(1).toLowerCase(), msg, e.getAuthor().getId());
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("delete")) {
            if(!Boolean.valueOf(e.getGuildProfile().getConfigValue(GuildConfig.MEMBER_CREATE_CMDS).toString())) {
                if(!e.hasPermission("command.commands.overpass")) {
                    return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
                }
            }
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "delete", "nome");
            }
            if (!e.getGuildProfile().hasCustomCommand(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Ops, parece que este comando não existe ^0^");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            CustomCommand cmd = e.getGuildProfile().getCustomCommand(args.get(1));

            if(!e.getAuthor().getId().equals(cmd.getCreator())) {
                if(!e.hasPermission("command.commands.overpass")) {
                    e.getChannel().sendMessage(EmojiList.WORRIED + " Você não é o dono deste comando portanto não a pode deletar!").queue();
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            e.sendMessage(EmojiList.CORRECT + " O comando ``" + args.get(1) + "`` foi deletado com sucesso.");
            e.getGuildProfile().deleteCustomCommand(args.get(1));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("import")) {
            if(!Boolean.valueOf(e.getGuildProfile().getConfigValue(GuildConfig.MEMBER_CREATE_CMDS).toString())) {
                if(!e.hasPermission("command.commands.overpass")) {
                    return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.commands.overpass");
                }
            }
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "import", "nome");
            }
            if(e.getGuildProfile().getCustomCommand(args.get(1)) != null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que já há um comando com esse nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            HashMap<Guild, CustomCommand> cmds = new HashMap<>();
            Main.getMutualGuilds(e.getAuthor()).stream().filter(gd -> !gd.getId().equals(e.getGuild().getId())).forEach(gd -> {
                CustomCommand pf = Main.getDatabase().getGuildProfile(gd).getCustomCommand(args.get(1));
                if(pf != null) {
                    cmds.put(gd, pf);
                }
            });

            if(cmds.size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que não há comandos para importar!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            TextChooser tc = new TextChooser(e, ":beginner: Comandos a importar");

            for(Guild x : cmds.keySet()) {
                tc.addOption("Guilda: " + x.getName() + " | Resposta: " + cmds.get(x).getMsg(), () -> importCommand(e, args.get(1), cmds.get(x)));
            }

            tc.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
         }

         if(args.get(0).equalsIgnoreCase("raw")) {
             if(args.getSize() < 2) {
                 return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "raw", "nome");
             }
             if(!e.getGuildProfile().hasCustomCommand(args.get(1))) {
                 e.sendMessage(EmojiList.WORRIED + " Oops, este comando não existe.");
                 return new CommandResult(CommandResultEnum.SUCCESS);
             }
             CustomCommand cmd = e.getGuildProfile().getCustomCommand(args.get(1));
             e.sendMessage(EmojiList.CORRECT + " Raw String do comando ``" + args.get(1) + "`` ```" +
             "{\"name\":\"" + args.get(1) + "\", \"author\":\"" + cmd.getCreator() + "\", \"value\":\"" + cmd.getMsg() + "\"}" + "```");
             return new CommandResult(CommandResultEnum.SUCCESS);
         }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

    public void importCommand(MessageEvent e, String title, CustomCommand cmd){
        if(e.getGuildProfile().getCustomCommand(title) != null) {
            e.sendMessage(EmojiList.WORRIED + " Oops, parece que já há um comando com esse nome!");
            return;
        }
        e.getGuildProfile().createCustomCommand(title, cmd.getMsg(), e.getAuthor().getId());
        e.sendMessage(EmojiList.CORRECT + " O comando ``" + title + "`` foi importado com sucesso.");
    }


}
