package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.enums.*;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 01/07/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ChatClearCommand implements CommandExecutor {

    @Command(command = "chatclear", description = "Limpe as mensagens do chat", parameters = {"quantidade"}, aliasses = {"clearchat", "cc"}, sendTyping = false, type = CommandType.ADMINISTRATION, isAllowedToDefault = false,
            usage = "a!chatclear 10\na!chatclear -bots")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("-bots")) {
            e.getChannel().getHistory().retrievePast(100).queue(msg -> {
                List<Message> msgs = new ArrayList<>();
                msg.forEach(bot -> {
                    if(bot.getAuthor().isBot() || bot.getRawContent().startsWith(e.getGuildProfile().getConfigValue(GuildConfig.PREFIX).toString())) {
                        msgs.add(bot);
                    }
                });

                if(msgs.size() <= 0) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, não há mensagens para deletar!");
                }else{
                    if(msgs.size() == 1) {
                        msgs.get(0).delete().queue();
                    }else{
                        e.getOriginEvent().getChannel().deleteMessages(msgs).queue();
                    }
                    e.sendMessage(EmojiList.CORRECT + " Foram removidas ``" + msgs.size() + "`` mensagens de bots!");
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(!NumberUtils.isCreatable(args.get(0))) {
            e.sendMessage(EmojiList.WORRIED + " Oops, a quantidade definida é invalida.\n**Valor máximo:** ``99`` - **Valor minímo:** ``0``");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(Integer.valueOf(args.get(0)) <= 0 || (Integer.valueOf(args.get(0))) > 99) {
            e.sendMessage((EmojiList.WORRIED + " Oops, a quantidade definida é invalida.\n**Valor máximo:** ``99`` - **Valor minímo:** ``0``"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        e.getChannel().getHistory().retrievePast(Integer.valueOf(args.get(0)) + 1).queue(msg -> e.getOriginEvent().getChannel().deleteMessages(msg).queue());
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
