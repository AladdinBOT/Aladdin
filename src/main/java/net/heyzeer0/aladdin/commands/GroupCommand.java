package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
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
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 21/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GroupCommand implements CommandExecutor {


    @Command(command = "group", aliasses = {"gp"}, description = "Crie grupos e adicione permissões.\n\nGrupos com nome de cargos serão automaticamente conectados.\nPermissões com ``*`` são aceitáveis, exemplo ``command.*``", parameters = {"create/delete/addperm/remperm/info/list/nodes"}, type = CommandType.ADMINISTRATION, isAllowedToDefault = false,
            usage = "a!group create admin\na!group delete admin\na!group addperm admin command.*\na!group remperm admin command.*\na!group info admin\na!group list\na!group nodes")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("criar")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome", "default");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) != null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que este grupo já existe!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().createGroup(new GroupProfile(args.get(1).toLowerCase(), Boolean.valueOf(args.get(2))));

            e.sendMessage(EmojiList.CORRECT + " Você criou com sucesso o grupo ``" + args.get(1).toLowerCase() + "`` e definiu a propriedade ``default`` para ``" + Boolean.valueOf(args.get(2)) + "``");
            e.sendMessage(":interrobang: Para adicionar um membro ao grupo use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "user addgroup " + args.get(1).toLowerCase() + "``");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("delete")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", "nome");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) == null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que este grupo não existe!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getGuildProfile().deleteGroup(args.get(1).toLowerCase())) {
                e.sendMessage(EmojiList.WORRIED + " Oops, este é o grupo padrão de sua guilda, você não pode remove-lo.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendMessage(EmojiList.CORRECT + " Você deletou com sucesso o grupo ``" + args.get(1).toLowerCase() + "``");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("addperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "addperm", "nome", "permissão");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1)) == null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que este grupo não existe!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!NodeManager.validNode(args.get(2))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que o nodo ``" + args.get(2) + "`` não existe, você pode listar todos utilizando ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "group nodes``");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().addGroupPermission(args.get(1), args.get(2));

            e.sendMessage(EmojiList.CORRECT + " Você adicionou a permissão ``" + args.get(2) + "`` para o grupo ``" + args.get(1) + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remperm")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remperm", "nome", "permissão");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1)) == null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que este grupo não existe!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getGuildProfile().removeGroupPermission(args.get(1), args.get(2));

            e.sendMessage(EmojiList.CORRECT + " Você removeu a permissão ``" + args.get(2) + "`` para o grupo ``" + args.get(1) + "``");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("info")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "remperm", "nome");
            }

            if(e.getGuildProfile().getGroupByName(args.get(1).toLowerCase()) == null) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que este grupo não existe!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            GroupProfile pf = e.getGuildProfile().getGroupByName(args.get(1).toLowerCase());

            String permissions = " ";

            if(pf.getPermissions().size() >= 1) {
                for(String x : pf.getPermissions()) {
                    permissions = permissions + "- " + x + "\n";
                }
            }else{ permissions = "*Não foram definidas permissões*"; }

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            boolean role = e.getGuild().getRolesByName(args.get(1), true).size() >= 1;

            b.setTitle(":beginner: Propriedades do grupo " + args.get(1).toLowerCase());
            b.setDescription("Grupo padrão: ``" + pf.isDefault() + "`` | anexado a role: ``" + role + "``");
            b.addField("Permissões:", permissions, false);

            if(!pf.isDefault()) {
                String users = "";

                if(role) {
                    users = " - membros do cargo ``" + e.getGuild().getRolesByName(args.get(1), true).get(0).getName() + "``";
                }

                if(e.getGuildProfile().getUser_group().size() >= 1) {
                    for(String x : e.getGuildProfile().getUser_group().keySet()) {
                        if(e.getGuildProfile().getUser_group().get(x).equalsIgnoreCase(pf.getId())) {
                            users = users + "- " + e.getJDA().getUserById(x).getName() + "\n";
                        }
                    }
                }
                b.addField("Membros:", users, false);
            }else{
                b.addField("Membros:", "- todos os membros da guilda", false);
            }

            b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            b.setTitle(":beginner: Listando os grupos da guilda " + e.getGuild().getName());
            b.setDescription("Para mais informações digite ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "guild``");

            String groups = "";

            for(String x : e.getGuildProfile().getGroups().keySet()) {
                groups = groups + " - **" + x + " **| default: ``" + e.getGuildProfile().getGroupByName(x).isDefault() + "``\n";
            }

            b.addField("Grupos Disponíveis", groups, true);
            b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("nodes")) {

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);

            b.setTitle(":beginner: Listando os nodos disponíveis");
            b.setDescription("Os nodos aceitam ``*`` para garantir seus subnodos.\nEx: ``command.*`` irá garantir acesso a todos os comandos.");

            for(String x : NodeManager.nodes.keySet()) {
                b.addField(x, StringUtils.join(NodeManager.getNode(x).getSubnodes(), "\n"), false);
            }

            b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            b.setTimestamp(e.getMessage().getCreationTime());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
