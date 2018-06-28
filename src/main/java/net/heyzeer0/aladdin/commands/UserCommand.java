package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
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

import java.util.List;

/**
 * Created by HeyZeer0 on 22/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class UserCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "user", description = "command.user.description", parameters = {"setgroup/addperm/remperm/info"}, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!user setgroup admin\na!user addperm command.* @HeyZeer0\na!user addperm -command.music @HeyZeer0\na!user remperm command.* @HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if(args.get(0).equalsIgnoreCase("addperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", lp.get("command.user.arg.1"), lp.get("command.user.arg.2"));
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    try{
                        if(e.getGuild().getMemberById(args.get(2)) != null) {
                            u = e.getGuild().getMemberById(args.get(2)).getUser();
                        }
                    }catch (Exception ex) {}
                }
            }

            if(!NodeManager.validNode(args.get(1).replace("-", ""))) {
                e.sendMessage(lp.get("command.user.addperm.error.invalidnode", args.get(1), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "group nodes"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(u == null) {
                e.sendMessage(lp.get("command.user.error.invaliduser"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().addUserOverride(u, args.get(1))) {
                e.sendMessage(lp.get("command.user.addperm.error.alreadyhas"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(lp.get("command.user.addperm.success", args.get(1), u.getName()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remperm", lp.get("command.user.arg.1"), lp.get("command.user.arg.2"));
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    try{
                        if(e.getGuild().getMemberById(args.get(2)) != null) {
                            u = e.getGuild().getMemberById(args.get(2)).getUser();
                        }
                    }catch (Exception ex) {}
                }
            }

            if(u == null) {
                e.sendMessage(lp.get("command.user.error.invaliduser"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().removeUserOverride(u, args.get(1))) {
                e.sendMessage(lp.get("command.user.remperm.invalidperm"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(lp.get("command.user.remperm.success", args.get(1), u.getName()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("setgroup")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", lp.get("command.user.setgroup.arg.1"), lp.get("command.user.setgroup.arg.2"));
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    try{
                        if(e.getGuild().getMemberById(args.get(2)) != null) {
                            u = e.getGuild().getMemberById(args.get(2)).getUser();
                        }
                    }catch (Exception ex) {}
                }
            }

            if(u == null) {
                e.sendMessage(lp.get("command.user.error.invaliduser"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).toLowerCase().equalsIgnoreCase("unset")) {
                e.getGuildProfile().removeUserGroup(u);
                e.sendMessage(lp.get("command.user.setgroup.success.2"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            GroupProfile pf = e.getGuildProfile().getGroupByName(args.get(1).toLowerCase());

            if(pf == null) {
                e.sendMessage(lp.get("command.user.setgroup.error.invalidgroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(pf.isDefault()) {
                e.sendMessage(lp.get("command.user.setgroup.error.defaultgroup"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuild().getRolesByName(pf.getId(), true).size() >= 1) {
                if(e.getGuild().getMember(u).getRoles().contains(e.getGuild().getRolesByName(pf.getId(), true).get(0))) {
                    e.sendMessage(lp.get("command.user.setgroup.error.alreadymember"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            e.getGuildProfile().updateUserGroup(u, pf.getId());

            e.sendMessage(lp.get("command.user.setgroup.success.1", u.getName(), pf.getId()));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
