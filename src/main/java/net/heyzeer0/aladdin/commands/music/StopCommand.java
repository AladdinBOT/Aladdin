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

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class StopCommand implements CommandExecutor {

    @Command(command = "stop", description = "command.music.stop.description", type = CommandType.MUSIC, isAllowedToDefault = false,
            usage = "a!stop")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!Main.getMusicManager().isConnected(e.getGuild())) {
            e.sendMessage(lp.get("command.music.stop.error.notconnected"));
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        e.sendMessage(lp.get("command.music.stop.success", Main.getMusicManager().getGuildController(e.getGuild()).queueFinish()));
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}