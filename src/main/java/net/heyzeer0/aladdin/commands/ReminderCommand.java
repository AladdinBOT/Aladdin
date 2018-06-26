package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ReminderCommand implements CommandExecutor {

    @Command(command = "remindme", description = "command.remindme.description", aliasses = {"remind"}, parameters = {"time(m/h)", "message"}, type = CommandType.MISCELLANEOUS,
            usage = "a!remindme 1m upvote\na!remidme 1h upvote", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        boolean minute = args.get(0).contains("m");

        try{
            Integer value = Integer.valueOf(args.get(0).replace("m", "").replace("h", ""));

            if(!minute && !e.getUserProfile().isPremiumActive()) {
                if(value > 5) {
                    e.sendMessage(String.format(lp.get("command.remindme.timelimit"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }
            if(minute && !e.getUserProfile().isPremiumActive()) {
                if(value > 300) {
                    e.sendMessage(String.format(lp.get("command.remindme.timelimit"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            long time = System.currentTimeMillis() + (minute ? (60000 * value) : (3600000 * value));

            Main.getDatabase().getServer().addReminder(new ReminderProfile(args.getCompleteAfter(1), time, e.getAuthor().getId()));
            e.sendMessage(String.format(lp.get("command.remindme.success"), args.get(0), args.getCompleteAfter(1)));

        }catch (Exception ex) { lp.get("command.remindme.error"); }

        return new CommandResult(CommandResultEnum.SUCCESS);
    }
}
