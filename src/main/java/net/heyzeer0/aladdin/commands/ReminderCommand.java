package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ReminderCommand implements CommandExecutor {

    @Command(command = "lembrar", description = "Defina lembretes para mais tarde", aliasses = {"lembrete"}, parameters = {"tempo(m/h)", "mensagem"}, type = CommandType.MISCELLANEOUS,
            usage = "a!lembrar 1m Dar upvote no aladdin\na!lembrar 1h Dar upvote no aladdin", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        boolean minute = args.get(0).contains("m");

        try{
            Integer value = Integer.valueOf(args.get(0).replace("m", "").replace("h", ""));

            if(!minute && !e.getUserProfile().isPremiumActive()) {
                if(value > 5) {
                    e.sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 5 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }
            if(minute && !e.getUserProfile().isPremiumActive()) {
                if(value > 300) {
                    e.sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 5 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            long time = System.currentTimeMillis() + (minute ? (60000 * value) : (3600000 * value));

            Main.getDatabase().getServer().addReminder(new ReminderProfile(args.getCompleteAfter(1), time, e.getAuthor().getId()));
            e.sendMessage(EmojiList.CORRECT + " Você definiu um lembrete para daqui ``" + args.get(0) + "`` com o motivo ``" + args.getCompleteAfter(1) + "``");

        }catch (Exception ex) { e.sendMessage(EmojiList.WORRIED + " Oops, parece que o tempo digitado é invalido.");}

        return new CommandResult(CommandResultEnum.SUCCESS);
    }
}
