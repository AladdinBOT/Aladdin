package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
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

    @Command(command = "commands", description = "command.commands.description", extra_perm = {"overpass"}, aliasses = {"cmds"}, parameters = {"create/delete/listargs/list/import/raw"}, type = CommandType.ADMNISTRATIVE,
            usage = "a!commands listargs\na!commands list\na!commands create ata Você é #random[feio,bonito]\na!commands import ata\na!commands delete ata\na!commands raw ata")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("listargs")) {
            Paginator ph = new Paginator(e, lp.get("command.commands.listargs.paginator.title"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page1"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page2"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page3"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page4"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page5"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page6"));
            ph.addPage(lp.get("command.commands.listargs.paginator.page7"));

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {
            if(e.getGuildProfile().getCommands().size() <= 0) {
                e.sendMessage(lp.get("command.commands.list.error.nocommands"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Paginator ph = new Paginator(e, lp.get("command.commands.list.paginator.title"));

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
                    pg = pg + String.format(lp.get("command.commands.list.paginator.page"), list.get(p).getKey(), user) + "\n";
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
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome", "mensagem");
            }
            if (e.getGuildProfile().hasCustomCommand(args.get(1))) {
                e.sendMessage(lp.get("command.commands.create.error.alreadyexist"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            String msg = args.getCompleteAfter(2);

            e.sendMessage(String.format(lp.get("command.commands.create.success"), args.get(1).toLowerCase()));
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
                e.sendMessage(lp.get("command.commands.delete.error.unknowncommand"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            CustomCommand cmd = e.getGuildProfile().getCustomCommand(args.get(1).toLowerCase());

            if(!e.getAuthor().getId().equals(cmd.getCreator())) {
                if(!e.hasPermission("command.commands.overpass")) {
                    e.sendMessage(lp.get("command.commands.delete.error.notowner"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            e.sendMessage(String.format(lp.get("command.commands.delete.success"), args.get(1).toLowerCase()));
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
                e.sendMessage(lp.get("command.commands.import.error.alreadyexist"));
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
                e.sendMessage(lp.get("command.commands.import.error.unknowncommand"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            TextChooser tc = new TextChooser(e, ":beginner: Comandos a importar");

            for(Guild x : cmds.keySet()) {
                tc.addOption(String.format(lp.get("command.commands.import.textchooser.option"), x.getName(), cmds.get(x).getMsg()), () -> importCommand(e, args.get(1), cmds.get(x), lp));
            }

            tc.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
         }

         if(args.get(0).equalsIgnoreCase("raw")) {
             if(args.getSize() < 2) {
                 return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "raw", "nome");
             }
             if(!e.getGuildProfile().hasCustomCommand(args.get(1))) {
                 e.sendMessage(lp.get("command.commands.raw.unknowncommand"));
                 return new CommandResult(CommandResultEnum.SUCCESS);
             }
             CustomCommand cmd = e.getGuildProfile().getCustomCommand(args.get(1));
             e.sendMessage(String.format(lp.get("command.commands.raw.success"), args.get(1), "{\"name\":\"" + args.get(1) + "\", \"author\":\"" + cmd.getCreator() + "\", \"value\":\"" + cmd.getMsg() + "\"}"));
             return new CommandResult(CommandResultEnum.SUCCESS);
         }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

    public void importCommand(MessageEvent e, String title, CustomCommand cmd, LangProfile lp){
        if(e.getGuildProfile().getCustomCommand(title) != null) {
            e.sendMessage(lp.get("command.commands.import.error.alreadyexist"));
            return;
        }
        e.getGuildProfile().createCustomCommand(title, cmd.getMsg(), e.getAuthor().getId());
        e.sendMessage(String.format(lp.get("command.commands.import.error.alreadyexist"), title));
    }


}
