package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class RepeatCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "repeat", description = "Altere o modo de repetição", aliasses = {"r"}, parameters = {"song/playlist/stop"}, extra_perm = {"overpass"}, type = CommandType.MUSIC,
            usage = "a!repeat song\na!repeat playlist\na!repeat stop")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!Boolean.valueOf(e.getGuildProfile().getConfigValue(GuildConfig.MEMBER_CAN_REPEAT).toString())) {
            if(!e.hasPermission("command.repeat.overpass")) {
                return new CommandResult(CommandResultEnum.MISSING_PERMISSION, "command.repeat.overpass");
            }
        }

        if(args.get(0).equalsIgnoreCase("stop") || args.get(0).equalsIgnoreCase("s")) {
            e.sendMessage(EmojiList.CORRECT + " O player de musica não ira repetir mais nada.");
            MusicManager.getManager(e.getGuild()).setRepeatMode(null);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("song") || args.get(0).equalsIgnoreCase("s")) {
            e.sendMessage(EmojiList.CORRECT + " O player de musica ira repetir a musica atual.");
            MusicManager.getManager(e.getGuild()).setRepeatMode(GuildTrackProfile.RepeatMode.SONG);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        if(args.get(0).equalsIgnoreCase("playlist") || args.get(0).equalsIgnoreCase("p")) {
            e.sendMessage(EmojiList.CORRECT + " O player de musica ira repetir a playlist atual.");
            MusicManager.getManager(e.getGuild()).setRepeatMode(GuildTrackProfile.RepeatMode.QUEUE);
            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }

}
