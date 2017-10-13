package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class UpvoteCommand implements CommandExecutor {


    @Command(command = "upvote", description = "Vote e ganhe premium por 5 dias", type = CommandType.INFORMATIVE,
            usage = "a!upvote")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        EmbedBuilder b = new EmbedBuilder();
        b.setTitle(":beginner: Como votar no Aladdin");
        b.setDescription(":one: Clique [aqui](https://discordbots.org/bot/321349548712656896) para ir até o site\n" +
                         ":two: Clique no botão login e entre com sua conta do discord.\n" +
                         ":three: Clique no botão **Upvote**\n" +
                         ":four: Aguarde 10 minutos para a ativação do seu premium.");
        b.setColor(Color.GREEN);
        b.setFooter("Powered by DiscordBots.org", "https://discordbots.org/images/logotrans.png");

        e.sendMessage(b);
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
