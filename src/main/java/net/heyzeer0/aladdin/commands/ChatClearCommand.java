package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HeyZeer0 on 01/07/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ChatClearCommand implements CommandExecutor {

    public static HashMap<String, Long> last_deletion = new HashMap<>();

    @Command(command = "chatclear", description = "command.chatclear.description", parameters = {"amount ou -bots"}, aliasses = {"clearchat", "cc"}, sendTyping = false, type = CommandType.ADMNISTRATIVE, isAllowedToDefault = false,
            usage = "a!chatclear 10\na!chatclear -bots")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("-bots")) {
            e.getChannel().getHistory().retrievePast(100).queue(msg -> {
                List<Message> msgs = new ArrayList<>();
                msg.forEach(bot -> {
                    if(bot.getAuthor().isBot() || bot.getContentRaw().startsWith(e.getGuildProfile().getConfigValue(GuildConfig.PREFIX).toString())) {
                        msgs.add(bot);
                    }
                });

                if(msgs.size() <= 0) {
                    e.sendMessage(lp.get("command.chatclear.nomessages"));
                }else{
                    if(msgs.size() == 1) {
                        msgs.get(0).delete().queue();
                    }else{
                        e.getOriginEvent().getChannel().deleteMessages(msgs).queue();
                    }
                    e.sendMessage(String.format(lp.get("command.chatclear.success.bot"), msgs.size()));
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(!NumberUtils.isCreatable(args.get(0))) {
            e.sendMessage(lp.get("command.chatclear.error.wrongamount"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(Integer.valueOf(args.get(0)) <= 0 || (Integer.valueOf(args.get(0))) > 99) {
            e.sendMessage(lp.get("command.chatclear.error.wrongamount"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        last_deletion.put(e.getGuild().getId(), System.currentTimeMillis());
        e.getChannel().getHistory().retrievePast(Integer.valueOf(args.get(0)) + 1).queue(msg -> e.getOriginEvent().getChannel().deleteMessages(msg).queue());
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
