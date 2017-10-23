package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.AkinatorProfile;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 23/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AkinatorCommand implements CommandExecutor {

    public static HashMap<String, Long> akinators = new HashMap<>();

    @Command(command = "akinator", description = "Pense em um um personagem e um gênio adivinhara quem é", type = CommandType.FUN,
            usage = "a!akinator")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(akinators.containsKey(e.getAuthor().getId())) {
            long atual = akinators.get(e.getAuthor().getId());
            if((System.currentTimeMillis() - atual) >= 15000) {
                akinators.remove(e.getAuthor().getId());
                System.out.println("timedout");
            }else{
                e.sendMessage(EmojiList.WORRIED + " Oops, você já esta em um akinator!");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
        }

        akinators.put(e.getAuthor().getId(), System.currentTimeMillis());

        Utils.runAsync(() -> {
            try{
                new AkinatorProfile(e);
            }catch (Exception ex) { ex.printStackTrace(); }
        });

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
