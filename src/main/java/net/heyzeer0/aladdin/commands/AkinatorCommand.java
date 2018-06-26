package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
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

    //ata

    public static HashMap<String, Long> akinators = new HashMap<>();

    @Command(command = "akinator", description = "command.akinator.description", type = CommandType.FUN,
            usage = "a!akinator")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lang) {
        if(akinators.containsKey(e.getAuthor().getId())) {
            long atual = akinators.get(e.getAuthor().getId());
            if((System.currentTimeMillis() - atual) >= 15000) {
                akinators.remove(e.getAuthor().getId());
            }else{
                e.sendMessage(lang.get("command.akinator.alreadyingame"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
        }

        akinators.put(e.getAuthor().getId(), System.currentTimeMillis());

        Utils.runAsync(() -> {
            try{
                new AkinatorProfile(e, lang);
            }catch (Exception ex) { ex.printStackTrace(); }
        });

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
