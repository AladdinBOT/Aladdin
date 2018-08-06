package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.enums.RepeatMode;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class RepeatCommand implements CommandExecutor {

    @Command(command = "repeat", description = "command.music.repeat.description", type = CommandType.MUSIC,
            usage = "a!repeat song\na!repeat s\na!repeat queue\na!repeat q\na!repeat stop\n a!repeat st", extra_perm = {"overpass"}, parameters = {"song/queue/stop"})
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!Boolean.valueOf(e.getGuildProfile().getConfigValue(GuildConfig.MEMBER_CAN_REPEAT).toString())) {
            if(!e.hasPermission("command.repeat.overpass")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.repeat.overpass");
            }
        }

        if(!Main.getMusicManager().isConnected(e.getGuild())) {
            e.sendMessage(lp.get("command.music.skip.error.notconnected"));
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if (Main.getMusicManager().isConnected(e.getGuild()) && !Main.getMusicManager().getGuildController(e.getGuild()).getChannelName().equals(e.getMember().getVoiceState().getChannel().getName())) {
            if(!e.hasPermission("command.repeat.overpass")) {
                e.sendMessage(lp.get("command.music.skip.error.notonchannel"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
        }

        if(args.get(0).equalsIgnoreCase("stop") || args.get(0).equalsIgnoreCase("st")) {
            e.sendMessage(lp.get("command.music.repeat.success.1"));

            Main.getMusicManager().getGuildController(e.getGuild()).changeRepeatMode(RepeatMode.OFF);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("song") || args.get(0).equalsIgnoreCase("s")) {
            e.sendMessage(lp.get("command.music.repeat.success.2"));

            Main.getMusicManager().getGuildController(e.getGuild()).changeRepeatMode(RepeatMode.SONG);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("queue") || args.get(0).equalsIgnoreCase("q")) {
            e.sendMessage(lp.get("command.music.repeat.success.3"));

            Main.getMusicManager().getGuildController(e.getGuild()).changeRepeatMode(RepeatMode.QUEUE);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }

}
