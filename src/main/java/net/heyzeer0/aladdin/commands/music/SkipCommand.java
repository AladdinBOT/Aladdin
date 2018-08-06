package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SkipCommand implements CommandExecutor {

    @Command(command = "skip", description = "command.music.skip.description", type = CommandType.MUSIC,
            usage = "a!skip")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
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

        GuildController c = Main.getMusicManager().getGuildController(e.getGuild());

        if(c.getCurrentTrack().getDJ().getId().equals(e.getAuthor().getId())) {
            e.sendPureMessage(lp.get("command.music.skip.success", e.getMember().getEffectiveName())).queue(m -> m.delete().queueAfter(30, TimeUnit.SECONDS));

            c.startNext(true);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        c.computeSkipVote(e.getAuthor());
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
