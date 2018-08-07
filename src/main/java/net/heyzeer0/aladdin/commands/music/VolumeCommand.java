package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class VolumeCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "volume", description = "command.music.volume.description", parameters = {"30 to 110"}, type = CommandType.MUSIC,
            usage = "a!volume 30", needPermission = false, aliasses = {"vol"})
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!e.getUserProfile().isPremiumActive()) {
            return new CommandResult((CommandResultEnum.NEED_PREMIUM));
        }

        try{
            Integer value = Integer.valueOf(args.get(0));

            if(!Main.getMusicManager().isConnected(e.getGuild())) {
                e.sendMessage(lp.get("command.music.skip.error.notconnected"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            if (Main.getMusicManager().isConnected(e.getGuild()) && !Main.getMusicManager().getGuildController(e.getGuild()).getChannelName().equals(e.getMember().getVoiceState().getChannel().getName())) {
                if(!e.hasPermission("command.skip.overpass")) {
                    e.sendMessage(lp.get("command.music.skip.error.notonchannel"));
                    return new CommandResult((CommandResultEnum.SUCCESS));
                }
            }

            if(!e.getAuthor().getId().equals(BotConfig.bot_owner)) {
                if(value < 30 || value > 110) {
                    e.sendMessage(lp.get("command.music.volume.error.invalidvalue"));
                    return new CommandResult((CommandResultEnum.SUCCESS));
                }
            }

            Main.getMusicManager().getGuildController(e.getGuild()).getPlayer().setVolume(value);

            e.sendMessage(lp.get("command.music.volume.success", value));
        }catch (Exception ex) {
            e.sendMessage(lp.get("command.music.volume.error.invalidvalue"));
        }

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
