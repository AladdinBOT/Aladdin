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

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 10/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LyricsCommand implements CommandExecutor {

    @Command(command = "lyrics", description = "command.music.lyrics.description", type = CommandType.MUSIC,
            usage = "a!lyrics", aliasses = {"l"})
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!Main.getMusicManager().isConnected(e.getGuild())) {
            e.sendMessage(lp.get("command.music.queue.error.notplaying"));
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        if(Main.getMusicManager().getGuildController(e.getGuild()).getCurrentTrack().getLyrics() == null) {
            e.sendMessage(lp.get("command.music.lyrics.error.notfound"));
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        String lyrics = Main.getMusicManager().getGuildController(e.getGuild()).getCurrentTrack().getLyrics();
        ArrayList<String> messages = new ArrayList<>();

        int chars = 10;
        String currentMessage = "";
        for(String x : lyrics.split("\n")) {
            if(chars + x.length() > 2000) {
                messages.add(currentMessage);
                currentMessage = "";
                chars = 10;
                continue;
            }
            chars+= x.length() + 2;

            currentMessage+=x + "\n";
        }

        if(currentMessage != null) messages.add(currentMessage);

        for(String msg : messages) {
            e.sendMessage("```HTTP\n" + msg + "\n```");
        }

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
