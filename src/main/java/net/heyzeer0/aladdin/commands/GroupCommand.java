package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.database.entities.profiles.GroupProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.permissions.NodeManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 21/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GroupCommand implements CommandExecutor {

    @Command(command = "group", aliasses = {"gp"}, description = "command.group.description", parameters = {"create/delete/addperm/remperm/info/list/nodes"}, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!group create admin\na!group delete admin\na!group addperm admin command.*\na!group remperm admin command.*\na!group info admin\na!group list\na!group nodes")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("create")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "groupname", "default");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) != null) {
                e.sendMessage(lp.get("command.group.create.groupexists"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().createGroup(new GroupProfile(args.get(1).toLowerCase(), e.getGuildProfile().getDefaultGroup().getPermissions(), Boolean.valueOf(args.get(2))));

            e.sendMessage(String.format(lp.get("command.group.create.success.1"), args.get(1).toLowerCase(), Boolean.valueOf(args.get(2))));
            e.sendMessage(String.format(lp.get("command.group.create.success.2"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "user addgroup" + args.get(1).toLowerCase()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("delete")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "groupname");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) == null) {
                e.sendMessage(lp.get("command.group.unknowngroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().deleteGroup(args.get(1).toLowerCase())) {
                e.sendMessage(lp.get("command.group.delete.defaultgroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(String.format(lp.get("command.group.delete.success"), args.get(1).toLowerCase()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("addperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", "groupname", "permission");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1)) == null) {
                e.sendMessage(lp.get("command.group.unknowngroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!NodeManager.validNode(args.get(2))) {
                e.sendMessage(String.format(lp.get("command.group.addperm.unknownnode"), args.get(2), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "group nodes"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().addGroupPermission(args.get(1), args.get(2));

            e.sendMessage(String.format(lp.get("command.group.addperm.success"), args.get(2), args.get(1)));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remperm", "groupname", "permission");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1)) == null) {
                e.sendMessage(lp.get("command.group.unknowngroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().removeGroupPermission(args.get(1), args.get(2));

            e.sendMessage(String.format(lp.get("command.group.remperm.success"), args.get(2), args.get(1)));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("info")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "info", "groupname");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) == null) {
                e.sendMessage(lp.get("command.group.unknowngroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            GroupProfile pf = e.getGuildProfile().getGroupByName(args.get(1).toLowerCase());

            String permissions = " ";

            if(pf.getPermissions().size() >= 1) {
                for(String x : pf.getPermissions()) {
                    permissions = permissions + "- " + x + "\n";
                }
            }else{ permissions = lp.get("command.group.info.embed.permissionsnotset"); }

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            boolean role = e.getGuild().getRolesByName(args.get(1), true).size() >= 1;

            b.setTitle(String.format(lp.get("command.group.info.embed.title"), args.get(1).toLowerCase()));
            b.setDescription(String.format(lp.get("command.group.info.embed.description"), pf.isDefault(), role));
            b.addField(lp.get("command.group.info.embed.field.1"), permissions, false);

            if(!pf.isDefault()) {
                String users = "";

                if(role) {
                    users = String.format(lp.get("command.group.info.embed.field.2.role"), e.getGuild().getRolesByName(args.get(1), true).get(0).getName());
                }

                if(e.getGuildProfile().getUser_group().size() >= 1) {
                    for(String x : e.getGuildProfile().getUser_group().keySet()) {
                        if(e.getGuildProfile().getUser_group().get(x).equalsIgnoreCase(pf.getId())) {
                            users = users + "- " + e.getJDA().getUserById(x).getName() + "\n";
                        }
                    }
                }
                b.addField(lp.get("command.group.info.embed.field.2"), users, false);
            }else{
                b.addField(lp.get("command.group.info.embed.field.2"), lp.get("command.group.info.embed.field.2.everyone"), false);
            }

            b.setFooter(String.format(lp.get("command.group.info.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            b.setTitle(String.format(lp.get("command.group.list.embed.title"), e.getGuild().getName()));
            b.setDescription(String.format(lp.get("command.group.list.embed.description"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX)));

            String groups = "";

            for(String x : e.getGuildProfile().getGroups().keySet()) {
                groups = groups + " - **" + x + " **| default: ``" + e.getGuildProfile().getGroupByName(x).isDefault() + "``\n";
            }

            b.addField(lp.get("command.group.list.embed.field"), groups, true);
            b.setFooter(String.format(lp.get("command.group.list.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("nodes")) {

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            b.setTitle(lp.get("command.group.nodes.embed.title"));
            b.setDescription(lp.get("command.group.nodes.embed.description"));

            for(String x : NodeManager.nodes.keySet()) {
                b.addField(x, StringUtils.join(NodeManager.getNode(x).getSubnodes(), "\n"), false);
            }

            b.setFooter(String.format(lp.get("command.group.nodes.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
