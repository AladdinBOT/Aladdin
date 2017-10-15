package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class StarboardCommand {

    @Command(command = "starboard", description = "Crie ou delete starboards", aliasses = {"sboard"}, parameters = {"criar/remover/list"}, type = CommandType.MISCELLANEOUS,
            usage = "a!starboard criar 3 #starboard\na!starboard remover 0\na!starboard list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("criar")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "criar", "quantidade necessária de emotes", "#canal");
            }

            if(e.getMessage().getMentionedChannels().size() < 1) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não mencionou nenhum canal!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            try{

                Integer amount = Integer.valueOf(args.get(1));
                TextChannel ch = e.getMessage().getMentionedChannels().get(0);

                new Reactioner(EmojiList.THINKING + " Adicione como reação nesta mensagem o emote que quer utilizar", e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                    String emote = v.getReactionEmote().getName() + "|" + v.getReactionEmote().getId();
                    if(e.getGuildProfile().getStarboards().containsKey(emote)) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o emote mencionado já pertence a outra starboard.");
                        return;
                    }
                    e.getGuildProfile().createStarboard(emote, amount, ch.getIdLong());

                    e.sendMessage(EmojiList.CORRECT + " Você criou com sucesso a starboard.");
                });

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a quantidade inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
