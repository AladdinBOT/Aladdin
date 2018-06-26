package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.commands.CommandManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandContainer;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 23/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class HelpCommand implements CommandExecutor {

    @Command(command = "help", description = "command.help.description", type = CommandType.INFORMATIVE,
            usage = "a!help\na!help group", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.getSize() >= 1) {
            if(CommandManager.commands.containsKey(args.get(0))) {

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setThumbnail("https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
                b.setTitle(String.format(lp.get("command.help.embed.help.title"), args.get(0)));
                b.setFooter(String.format(lp.get("command.help.embed.help.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
                CommandContainer cmd = CommandManager.commands.get(args.get(0));
                b.addField(lp.get("command.help.embed.help.field.1"), lp.get(cmd.getAnnotation().description()), false);
                b.addField(lp.get("command.help.embed.help.field.2"), cmd.getAnnotation().usage(), false);
                if(!cmd.getAnnotation().aliasses()[0].equals("none")) {
                    b.addField(lp.get("command.help.embed.help.field.3"), StringUtils.join(cmd.getAnnotation().aliasses(), ", "), false);
                }
                if(cmd.getAnnotation().extra_perm()[0].equalsIgnoreCase("none")) {
                    b.addField(lp.get("command.help.embed.help.field.4"), "command." + cmd.getAnnotation().command(), false);
                }else{
                    String permissioes = " - command." + cmd.getAnnotation().command() + "\n";
                    for(String x : cmd.getAnnotation().extra_perm()) {
                        permissioes = permissioes + "- " + x + "\n";
                    }
                    b.addField(lp.get("command.help.embed.help.field.5"), permissioes, false);
                }

                e.sendMessage(b);
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            if(CommandManager.aliases.containsKey(args.get(0))) {
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setThumbnail("http://www.impactaustin.org/assets/images/icon_check.png");
                b.setTitle(String.format(lp.get("command.help.embed.help.title"), args.get(0)));
                b.setFooter(String.format(lp.get("command.help.embed.help.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
                CommandContainer cmd = CommandManager.aliases.get(args.get(0));
                b.addField(lp.get("command.help.embed.help.field.1"), lp.get(cmd.getAnnotation().description()), false);
                b.addField(lp.get("command.help.embed.help.field.2"), cmd.getAnnotation().usage(), false);
                if(!cmd.getAnnotation().aliasses()[0].equals("none")) {
                    b.addField(lp.get("command.help.embed.help.field.3"), StringUtils.join(cmd.getAnnotation().aliasses(), ", "), false);
                }

                if(cmd.getAnnotation().extra_perm()[0].equalsIgnoreCase("none")) {
                    b.addField(lp.get("command.help.embed.help.field.4"), "command." + cmd.getAnnotation().command(), false);
                }else{
                    String permissioes = " - command." + cmd.getAnnotation().command() + "\n";
                    for(String x : cmd.getAnnotation().extra_perm()) {
                        permissioes = permissioes + "- " + x + "\n";
                    }
                    b.addField(lp.get("command.help.embed.help.field.5"), permissioes, false);
                }

                e.sendMessage(b);
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
        }
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setAuthor(lp.get("command.help.embed.author"), null, "https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
        b.setDescription(String.format(lp.get("command.help.embed.description"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "help {command}"));

        HashMap<CommandType, ArrayList<String>> comandos = new HashMap<>();

        for(CommandType tp : CommandType.values()) {
            if(tp != CommandType.BOT_ADMIN)
                comandos.put(tp, new ArrayList<>());
        }

        Integer x = 0;

        for(String cmds : CommandManager.commands.keySet()) {
            CommandContainer cn = CommandManager.commands.get(cmds);
            if(cn.getAnnotation().type() != CommandType.BOT_ADMIN) {
                comandos.get(cn.getAnnotation().type()).add("``" + cmds + "``");
                x++;
            }
        }

        b.setFooter(String.format(lp.get("command.help.embed.footer"), x), null);

        comandos.keySet().forEach(key -> b.addField(key.getEmoji() + " | " + key.toString(), StringUtils.join(comandos.get(key), ", "), false));

        e.sendMessage(b);
        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
