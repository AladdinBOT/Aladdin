package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.GiveawayManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 14/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class    GiveawayCommand implements CommandExecutor {

    @Command(command = "giveaway", description = "Faça sorteios automaticos!", parameters = {"criar"}, type = CommandType.MISCELLANEOUS,
            usage = "a!giveaway criar 1h 1 Boné dahora\n", isAllowedToDefault = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("criar")) {
            if(args.getSize() < 4) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "criar", "tempo(m/h)", "quantidade de ganhadores", "descrição");
            }

            boolean minute = args.get(1).contains("m");

            try{

                Integer value = Integer.valueOf(args.get(1).replace("m", "").replace("h", ""));

                if(!minute && !e.getUserProfile().isPremiumActive()) {
                    if(value > 24) {
                        e.sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 24 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``");
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }
                }
                if(minute && !e.getUserProfile().isPremiumActive()) {
                    if(value > 1440) {
                        e.sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 24 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``");
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }
                }

                long time = System.currentTimeMillis() + (minute ? (60000 * value) : (3600000 * value));

                GiveawayManager.createGiveway(args.getCompleteAfter(3), time, Integer.valueOf(args.get(2)), e);
                e.getMessage().delete().queue();

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a quantidade de ganhadores inserida ou o tempo é invalido");
            }

            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }

}
