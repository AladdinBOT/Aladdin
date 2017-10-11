package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;

/**
 * Created by HeyZeer0 on 07/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class InviteCommand implements CommandExecutor {

    @Command(command = "invite", description = "Me chame para sua guilda!", type = CommandType.INFORMATIVE,
            usage = "a!invite")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.CYAN);
        b.setAuthor("Sobre Aladdin", null, "https://media.tenor.com/images/9a5178a7b636e201da025b7e41f8e2a2/tenor.gif");
        b.setDescription("Olá meu nome é Aladdin, caso você não saiba eu fui inspirado no anime [Magi](https://pt.wikipedia.org/wiki/Magi_(mang%C3%A1), eu tenho como foco ações administrativas porém posso ser bastante útil em outras coisas variantes desde diversão até musica, sendo desenvolvido pelo ``HeyZeer0#0190``. \nEm minha aventura eu sou acompanhado pelos meus grandes amigos Ali babá e Morgiana, talvez você encontre eles por aí. Meu objetivo é aprender mais e mais sobre este mundo, você pode me chamar para sua guilda clicando [aqui](https://discordapp.com/oauth2/authorize?client_id=321349548712656896&scope=bot&permissions=2146958463)!\nEae, gostaria de ser meu amigo?");
        b.setImage("https://s-media-cache-ak0.pinimg.com/originals/97/a6/4d/97a64d7741a1fe2ad187fa31a5d3e276.jpg");
        b.setFooter("Aladdin v" + Main.version, e.getJDA().getSelfUser().getAvatarUrl());
        e.sendMessage(b);
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
