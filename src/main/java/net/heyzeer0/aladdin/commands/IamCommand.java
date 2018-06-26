package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HeyZeer0 on 07/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class IamCommand implements CommandExecutor {

    @Command(command = "iam", description = "command.iam.description", extra_perm = {"manage"}, parameters = {"role/list/create/addrole/remrole/delete"}, type = CommandType.ADMNISTRATIVE,
            usage = "a!iam nsfw\na!iam create nsfw\na!iam addrole nsfw NSFW\na!iam remrole nsfw NSFW\na!iam delete nsfw\na!iam list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if(!e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).hasPermission(Permission.MANAGE_ROLES)) {
            e.sendMessage(lp.get("command.iam.missingpermission"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("create")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.iam.manage");
            }

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome");
            }

            String name = args.getCompleteAfter(1);

            if(e.getGuildProfile().iamExists(name)) {
                e.sendMessage(lp.get("command.iam.create.alreadyexist"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().createIam(name);

            e.sendMessage(String.format(lp.get("command.iam.create.success"), name, e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "iam addrole " + name + " role"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("delete")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.iam.manage");
            }

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "delete", "nome");
            }

            String name = args.getCompleteAfter(1);

            if(!e.getGuildProfile().iamExists(name)) {
                e.sendMessage(lp.get("command.iam.create.alreadyexist"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().deleteIam(name);

            e.sendMessage(String.format(lp.get("command.iam.delete.success"), name));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("addrole")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.iam.manage");
            }

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addrole", "iam", "role");
            }

            String iam = args.get(1);

            if(!e.getGuildProfile().iamExists(iam)) {
                e.sendMessage(lp.get("command.iam.create.alreadyexist"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Role role = null;

            if(e.getMessage().getMentionedRoles().size() >= 1) {
                role = e.getMessage().getMentionedRoles().get(0);
            }else if (e.getGuild().getRolesByName(args.getCompleteAfter(2), true).size() >= 1){
                role = e.getGuild().getRolesByName(args.getCompleteAfter(2), true).get(0);
            }else if (e.getGuild().getRoleById(args.get(2)) != null) {
                role = e.getGuild().getRoleById(args.get(2));
            }

            if(role == null) {
                e.sendMessage(lp.get("command.iam.invalidrole"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().size() <= 0) {
                e.sendMessage(lp.get("command.iam.addrole.invalidhierarchy"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(role.getPosition() >= e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition()) {
                e.sendMessage(lp.get("command.iam.addrole.invalidhierarchy"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuildProfile().addRoleToIam(iam, role.getId())) {
                e.sendMessage(String.format(lp.get("command.iam.addrole.sucess"), role.getName(), iam));
            }else{
                e.sendMessage(lp.get("command.iam.addrole.alreadyonit"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remrole")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.manage");
            }

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remrole", "iam", "role");
            }

            String iam = args.get(1);

            if(!e.getGuildProfile().iamExists(iam)) {
                e.sendMessage(lp.get("command.iam.create.alreadyexist"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Role role = null;

            if(e.getMessage().getMentionedRoles().size() >= 1) {
                role = e.getMessage().getMentionedRoles().get(0);
            }else if (e.getGuild().getRolesByName(args.getCompleteAfter(2), false).size() >= 1){
                role = e.getGuild().getRolesByName(args.getCompleteAfter(2), false).get(0);
            }else if (e.getGuild().getRoleById(args.get(2)) != null) {
                role = e.getGuild().getRoleById(args.get(2));
            }

            if(role == null) {
                e.sendMessage(lp.get("command.iam.invalidrole"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuildProfile().removeRoleFromIam(iam, role.getId())) {
                e.sendMessage(String.format(lp.get("command.iam.remrole.success"), role.getName(), iam));
            }else{
                e.sendMessage(lp.get("command.iam.remrole.notpart"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("list")) {
            if(e.getGuildProfile().getIam_profiles().size() <= 0) {
                e.sendMessage(lp.get("command.iam.list.noiams"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            EmbedBuilder b = new EmbedBuilder();
            b.setTitle(lp.get("command.iam.list.embed.title"));
            b.setColor(Color.GREEN);
            b.setFooter(String.format(lp.get("command.iam.list.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
            b.setDescription(String.format(lp.get("command.iam.list.embed.description"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "iam role"));

            HashMap<String, ArrayList<String>> iams = e.getGuildProfile().getIam_profiles();


            for(String k : iams.keySet()) {
                String roles = "";
                for(String id : iams.get(k)) {
                    roles = roles + "<@&" + id + "> ";
                }

                b.addField(k, roles, false);
            }

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(e.getGuildProfile().iamExists(args.getCompleteAfter(0))) {
            List<Role> roles = new ArrayList<>();

            for(String x : e.getGuildProfile().getIamRoles(args.getCompleteAfter(0))) {
                Role r = e.getGuild().getRoleById(x);

                if(r != null) {

                    if(e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().size() <= 0) {
                        continue;
                    }

                    if(r.getPosition() >= e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition()) {
                        continue;
                    }

                    roles.add(r);
                }
            }

            boolean remove = false;

            for (Role rl : roles) {
                if(e.getMember().getRoles().contains(rl)) {
                    remove = true;
                }
            }

            if(remove) {
                e.getGuild().getController().removeRolesFromMember(e.getMember(), roles).queue();
                e.sendMessage(String.format(lp.get("command.iam.success.1"), args.getCompleteAfter(0)));
            }else{
                e.getGuild().getController().addRolesToMember(e.getMember(), roles).queue();
                e.sendMessage(String.format(lp.get("command.iam.success.2"), EmojiList.CORRECT + " Agora você faz parte de ``" + args.getCompleteAfter(0) + "``"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }


        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
