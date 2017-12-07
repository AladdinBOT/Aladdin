package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Role;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 07/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class IamCommand implements CommandExecutor {

    @Command(command = "iam", description = "Permite que membros assumam cargos por conta própria", extra_perm = {"manage"}, parameters = {"role/list/create/addrole/remrole/delete"}, type = CommandType.ADMNISTRATIVE,
            usage = "a!iam nsfw\na!iam create nsfw\na!iam addrole nsfw NSFW\na!iam remrole nsfw NSFW\na!iam delete nsfw\na!iam list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("create")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
            }

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome");
            }

            String nome = args.getCompleteAfter(1);

            if(e.getGuildProfile().iamExists(nome)) {
                e.sendMessage(EmojiList.WORRIED + " Oops, já existe um iam com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().createIam(nome);

            e.sendMessage(EmojiList.CORRECT + " Você criou com sucesso o iam ``" + nome + "``\n" + ":interrobang: Para adicionar um cargo utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "iam addrole " + nome + " nomedocargo``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("delete")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
            }

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "delete", "nome");
            }

            String nome = args.getCompleteAfter(1);

            if(!e.getGuildProfile().iamExists(nome)) {
                e.sendMessage(EmojiList.WORRIED + " Oops, não existe um iam com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().deleteIam(nome);

            e.sendMessage(EmojiList.CORRECT + " Você deletou com sucesso o iam ``" + nome + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("addrole")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
            }

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addrole", "iam", "role");
            }

            String iam = args.get(1);

            if(!e.getGuildProfile().iamExists(iam)) {
                e.sendMessage(EmojiList.WORRIED + " Oops, não existe um iam com este nome!");
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
                e.sendMessage(EmojiList.WORRIED + " Oops, o cargo inserido é invalido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, o meu cargo precisa ser maior que o cargo definido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(role.getPosition() >= e.getGuild().getMemberById(e.getJDA().getSelfUser().getId()).getRoles().get(0).getPosition()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, o meu cargo precisa ser maior que o cargo definido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuildProfile().addRoleToIam(iam, role.getId())) {
                e.sendMessage(EmojiList.CORRECT + " Você adicionou o cargo ``" + role.getName() + "`` para o iam ``" + iam + "`` com sucesso.");
            }else{
                e.sendMessage(EmojiList.WORRIED + " Oops, o cargo definido já é parte deste iam.");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remrole")) {
            if(!e.hasPermission("command.iam.manage")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.command.create");
            }

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remrole", "iam", "role");
            }

            String iam = args.get(1);

            if(!e.getGuildProfile().iamExists(iam)) {
                e.sendMessage(EmojiList.WORRIED + " Oops, não existe um iam com este nome!");
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
                e.sendMessage(EmojiList.WORRIED + " Oops, o cargo inserido é invalido.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getGuildProfile().removeRoleFromIam(iam, role.getId())) {
                e.sendMessage(EmojiList.CORRECT + " Você removeu o cargo ``" + role.getName() + "`` para o iam ``" + iam + "`` com sucesso.");
            }else{
                e.sendMessage(EmojiList.WORRIED + " Oops, o cargo definido não é parte deste iam.");
            }

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
                    e.getGuild().getController().removeRolesFromMember(e.getMember(), rl).queue();
                    remove = true;
                }else{
                    e.getGuild().getController().addRolesToMember(e.getMember(), rl).queue();
                }
            }

            if(remove) {
                e.sendMessage(EmojiList.CORRECT + " Agora você não faz parte do iam ``" + args.getCompleteAfter(0) + "``");
            }else{
                e.sendMessage(EmojiList.CORRECT + " Agora você faz parte do iam ``" + args.getCompleteAfter(0) + "``");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }


        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
