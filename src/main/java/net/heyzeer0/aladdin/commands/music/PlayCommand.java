package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayCommand implements CommandExecutor {

    @Command(command = "play", description = "command.music.play.description", type = CommandType.MUSIC, parameters = {"name/url"},
            usage = "a!play Thunder - Imagine Dragons", aliasses = {"p"})
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
         e.sendPureMessage(lp.get("command.music.play.success", args.getCompleteAfter(0))).queue(msg -> {
             Main.getMusicManager().addToQueue(e.getAuthor(), msg, args.getCompleteAfter(0));
             msg.delete().queueAfter(30, TimeUnit.SECONDS);
        });

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
