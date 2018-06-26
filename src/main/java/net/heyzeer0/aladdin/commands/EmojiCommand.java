package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class EmojiCommand implements CommandExecutor {

    @Command(command = "emoji", description = "Obtenha informações sobre o emoji indicado", parameters = {"emoji"}, type = CommandType.BOT_ADMIN,
            usage = "a!emoji :smiley:")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if(e.getMessage().getEmotes().size() > 0) {
            e.getMessage().getEmotes().forEach(em -> e.sendMessage(new EmbedBuilder().setTitle(":beginner: Informações sobre o emoji " + em.getName()).addField("Id:", em.getId(), true).setDescription("Uso completo ``<:" + em.getName() + ":" + em.getId() + ">``").setThumbnail(em.getImageUrl()).setColor(Color.GREEN)));

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        e.sendMessage(EmojiList.WORRIED + " Oops, parece que você não mencionou nenhum emoji.");

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
