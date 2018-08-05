package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class StopCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "stop", description = "Limpe toda a playlist atual", type = CommandType.MUSIC, isAllowedToDefault = false,
            usage = "a!stop")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!e.getGuild().getAudioManager().isConnected()) {
            e.sendMessage(EmojiList.WORRIED + " Oops, eu não estou conectado a nenhum canal!");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        e.sendMessage(EmojiList.CORRECT + " Você limpou ``" + (Main.getMusicManger().getManager(e.getGuild()).stop() + 1) + "`` musicas da playlist.");
        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
