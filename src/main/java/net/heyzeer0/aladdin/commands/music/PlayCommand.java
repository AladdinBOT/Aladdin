package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.profiles.AudioLoaderProfile;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "play", description = "Adicione uma musica a playlist", aliasses = {"p"}, parameters = {"nome ou url"}, type = CommandType.MUSIC,
            usage = "a!play Thunder")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if (e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().getConnectedChannel().equals(e.getMember().getVoiceState().getChannel())) {
            e.sendMessage(EmojiList.WORRIED + " Você não esta conectado ao meu canal!");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        e.getChannel().sendMessage(EmojiList.CORRECT + " Procurando por músicas com o seguinte argumento: ``" + args.getCompleteAfter(0) + "``").queue(msg -> AudioLoaderProfile.loadAndPlay(e.getAuthor(), msg, args.getCompleteAfter(0), false));

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
