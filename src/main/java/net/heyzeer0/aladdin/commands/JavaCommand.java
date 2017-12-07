package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.JavaEvaluation;

import java.awt.*;

/**
 * Created by HeyZeer0 on 22/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class JavaCommand implements CommandExecutor {

    @Command(command = "java", description = "Emule funções em java.", aliasses = {"eval", "evaluate"}, parameters = {"código"}, type = CommandType.BOT_ADMIN,
            usage = "a!java return \"oi\";")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        String toEval = args.getComplete();

        if(toEval.startsWith("-cl")) {
            toEval = toEval.replace("-cl ", "");
            e.sendMessage("**Resultado**:\n```" + JavaEvaluation.eval(toEval, e) + "```");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        EmbedBuilder b = new EmbedBuilder();
        b.setTitle(":coffee: Simulação em Java", null);
        b.setColor(Color.YELLOW);
        b.addField("Comando:", "``" + toEval + "``", false);
        b.addField("Resultado:", JavaEvaluation.eval(toEval, e), false);
        b.setTimestamp(e.getMessage().getCreationTime());
        b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getAvatarUrl());

        e.getChannel().sendMessage(new MessageBuilder().setEmbed(b.build()).build()).queue();

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
