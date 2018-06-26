package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.OverwatchManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.OverwatchPlayer;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class OverwatchCommand implements CommandExecutor{

    @Command(command = "overwatch", description = "command.overwatch.description", aliasses = {"ow"}, parameters = {"profile", "user#id"}, type = CommandType.FUN,
            usage = "a!overwatch profile HeyZeer0#1903")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("profile")) {

            String user = args.get(1).replace("#", "-");

            if(!user.contains("-")) {
                e.sendMessage(lp.get("command.overwatch.profile.invaliduser"));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            Utils.runAsync(() -> {
                try{
                    OverwatchPlayer pf = OverwatchManager.getUserProfile(user);

                    if(pf == null) {
                        e.sendMessage(String.format(lp.get("command.overwatch.profile.invaliduser.2"), user));
                        return;
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.GREEN);
                    b.setAuthor(String.format(lp.get("command.overwatch.profile.embed.title"), user), null, "http://i.imgur.com/YZ4w2ey.png");
                    b.setDescription("Powered by [Overwatch-api](https://github.com/alfg/overwatch-api)");
                    b.setThumbnail(pf.getPortrait());
                    b.setFooter(String.format(lp.get("command.overwatch.profile.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
                    b.setTimestamp(e.getMessage().getCreationTime());

                    b.addField(":pen_ballpoint: | " + lp.get("command.overwatch.profile.embed.field.profile") + ":", "**" + lp.get("command.overwatch.profile.embed.field.level") + ":** " + pf.getLevel(), true);
                    b.addField("<:empty:363753754874478602>", "**" + lp.get("command.overwatch.profile.embed.field.rank") + ":** " + (pf.getCompetitiveRank() != null ? pf.getCompetitiveRank() : "Unranked"), true);
                    b.addField(":trophy: | " + lp.get("command.overwatch.profile.embed.field.ranked") + ":", "**" + lp.get("command.overwatch.profile.embed.field.wins") + ":** " + (pf.getCompetitiveWins() != null ? pf.getCompetitiveWins() : "0"), true);
                    b.addField("<:empty:363753754874478602>", "**" + lp.get("command.overwatch.profile.embed.field.losses") + ":** " + (pf.getCompetitiveLosts() != null ? pf.getCompetitiveLosts() : "0"), true);
                    b.addField(":video_game: | " + lp.get("command.overwatch.profile.embed.field.quickplay") + ":", "**" + lp.get("command.overwatch.profile.embed.field.wins") + ":** " + (pf.getQuickplayWins() != null ? pf.getQuickplayWins() : "0"), true);
                    b.addBlankField(true);
                    b.addField(":clock1: | " + lp.get("command.overwatch.profile.embed.field.time") + ":", "**" + lp.get("command.overwatch.profile.embed.field.ranked") + ":** " + (pf.getCompetitiveTime() != null ? pf.getCompetitiveTime() : "0"), true);
                    b.addField("<:empty:363753754874478602>", "**" + lp.get("command.overwatch.profile.embed.field.quickplay") + ":** " + (pf.getQuickplayTime() != null ? pf.getQuickplayTime() : "0"), true);

                    e.sendMessage(b);

                }catch (Exception ex) { e.sendMessage(String.format(lp.get("command.overwatch.profile.error"), ex.getMessage()));}
            });

            return new CommandResult((CommandResultEnum.SUCCESS));
        }
        return new CommandResult((CommandResultEnum.NOT_FOUND));
    }


}
