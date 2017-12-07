package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.database.entities.profiles.GroupProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.permissions.NodeManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.util.List;

/**
 * Created by HeyZeer0 on 22/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class UserCommand implements CommandExecutor {

    @Command(command = "user", description = "Defina grupos ou permissões a membros\n\nPermissões com ``*`` são aceitáveis, exemplo ``command.*``\nPermissões com ``-`` retirarão acesso a ela, exemplo ``-command.music``", parameters = {"setgroup/addperm/remperm/info"}, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!user setgroup admin\na!user addperm command.* @HeyZeer0\na!user addperm -command.music @HeyZeer0\na!user remperm command.* @HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("addperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", "permissão", "usuário");
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    if(e.getGuild().getMemberById(args.get(2)) != null) {
                        u = e.getGuild().getMemberById(args.get(2)).getUser();
                    }
                }
            }

            if(!NodeManager.validNode(args.get(1).replace("-", ""))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que o nodo ``" + args.get(1) + "`` não existe, você pode listar todos utilizando ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "group nodes``");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(u == null) {
                e.sendMessage(EmojiList.WORRIED + " Ops, o usuário definido é invalido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().addUserOverride(u, args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Ops, parece que o usuário já possuia esta permissão.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(EmojiList.CORRECT + " Você adicionou a permissão ``" + args.get(1) + "`` com sucesso ao usuário ``" + u.getName() + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remperm", "permissão", "usuário");
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    if(e.getGuild().getMemberById(args.get(2)) != null) {
                        u = e.getGuild().getMemberById(args.get(2)).getUser();
                    }
                }
            }

            if(u == null) {
                e.sendMessage(EmojiList.WORRIED + " Ops, o usuário definido é invalido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().removeUserOverride(u, args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Ops, parece que o usuário não possuia esta permissão.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(EmojiList.CORRECT + " Você removeu a permissão ``" + args.get(1) + "`` com sucesso do usuário ``" + u.getName() + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("setgroup")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", "grupo", "usuário");
            }

            User u = null;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                List<Member> user = e.getGuild().getMembersByName(args.get(2), true);
                if(user.size() >= 1) {
                    u = user.get(0).getUser();
                }else{
                    if(e.getGuild().getMemberById(args.get(2)) != null) {
                        u = e.getGuild().getMemberById(args.get(2)).getUser();
                    }
                }
            }

            if(u == null) {
                e.sendMessage(EmojiList.WORRIED + " Ops, o usuário definido é invalido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(args.get(1).toLowerCase().equalsIgnoreCase("unset")) {
                e.getGuildProfile().removeUserGroup(u);
                e.sendMessage(EmojiList.CORRECT + " Você removeu o grupo do usuário com sucesso.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            GroupProfile pf = e.getGuildProfile().getGroupByName(args.get(1).toLowerCase());

            if(pf == null) {
                e.sendMessage(EmojiList.WORRIED + " Ops, grupo inserido é invalido");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(pf.isDefault()) {
                e.sendMessage(EmojiList.WORRIED + " Ops, o grupo definido é o padrão, todos possuem acesso ao mesmo.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuild().getRolesByName(pf.getId(), true).size() >= 1) {
                if(e.getGuild().getMember(u).getRoles().contains(e.getGuild().getRolesByName(pf.getId(), true).get(0))) {
                    e.sendMessage(EmojiList.WORRIED + " Ops, o usuário indicado já faz parte desse grupo.");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            e.getGuildProfile().updateUserGroup(u, pf.getId());

            e.sendMessage(EmojiList.CORRECT + " Você adicionou o usuário ``" + u.getName() + "`` com sucesso ao grupo ``" + pf.getId() + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
