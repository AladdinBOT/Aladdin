package net.heyzeer0.aladdin.events.listeners;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.heyzeer0.aladdin.Main;

import java.awt.*;

/**
 * Created by HeyZeer0 on 22/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GuildListener {

    public static void onGuildJoin(GuildJoinEvent e) {
        if(e.getGuild().getSelfMember().hasPermission(e.getGuild().getDefaultChannel(), Permission.MESSAGE_WRITE)) {
            e.getGuild().getDefaultChannel().sendMessage("Olá meu nome é Aladdin, parece que você quer me acompanhar em minha jornada, vou te explicar os básicos.\n" +
                    "\n" +
                    "Eu sou um bot administrativo bastante complexo movido a grupos e permissões customizaveis, vou lhe explicar em breves palavras como este sistema funciona\n" +
                    "Para criar um grupo você pode utilizar o comando ``a!group create [nome]`` lembre-se que grupos com nome de cargos irão automaticamente ser conectados.\n" +
                    "Para adicionar uma permissão a um grupo utilize o comando ``a!group addperm [nome] [permissão]`` as permissões são chamadas de nodes e aceitam sufixos como ``*`` para garantir todas as permisssões daquele node, por exemplo se você utilizar a permissão ``command.*`` isto irá garantir acesso a todos os comandos disponíveis, bom, você pode listar todos os nodes digitando ``a!group nodes``.\n" +
                    "\n" +
                    "Eu sei que você em algum momento vai querer retirar permisssão de algum membro para determinada coisa, mas calma, não precisa criar um novo grupo para isso, para retirar ou adicionar acesso você pode utilizar o comando ``a!user addperm -[permissão] [@usuário]`` para remover a permissão de um usuário onde ``-`` indica que ele não tera acesso de forma alguma a permissão definida, supondo que você já entendeu, se quiser adicionar permissões a um usuário apenas remova o ``-``.\n" +
                    "\n" +
                    "AHHHHHHHHH, já ia me esquecendo, o que adianta criar grupos sem adicionar membros ^-^? para definir o grupo de um usuário digite ``a!user setgroup [grupo] [@usuário]``.\n" +
                    "\n" +
                    "Você também pode alterar algumas configurações minhas, de uma olhada no comando a!config se estiver interessado, você pode obter alguns exemplos usando ``a!help config``.\n" +
                    "\n" +
                    "Espero que se divirta comigo :smiley: e se precisar de ajuda, entre na minha guilda! https://discord.gg/ANVp6qv").queue();
        }

        e.getJDA().getSelfUser().getManager().setName("Aladdin").queue();
        Main.getLogger().embed(":house: Guild Join Event", "Guilda ``" + e.getGuild().getName() + "``\nUsers ``" + e.getGuild().getMembers().size() + "``\nDono ``" + e.getGuild().getOwner().getUser().getName() + "#" + e.getGuild().getOwner().getUser().getDiscriminator() + "``", Color.GREEN);


        Main.getDatabase().getGuildProfile(e.getGuild());
    }

    public static void onGuildLeave(GuildLeaveEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).deleteAsync();
        Main.getLogger().embed(":house: Guild Leave Event", "Guilda ``" + e.getGuild().getName() + "``\nUsers ``" + e.getGuild().getMembers().size() + "``", Color.GREEN);
    }

    public static void onMemberLeave(GuildMemberLeaveEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).cleanUserData(e.getMember().getUser());
    }

    public static void onOwnerUpdate(GuildUpdateOwnerEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).updateGuildOwner(e.getGuild().getOwner().getUser().getId());
    }

}
