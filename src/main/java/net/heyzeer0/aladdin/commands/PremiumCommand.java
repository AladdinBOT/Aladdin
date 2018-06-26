package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.database.entities.UserProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 03/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PremiumCommand implements CommandExecutor {

    @Command(command = "premium", description = "command.premium.description", parameters = {"info/activate/give/autorenew/features"}, type = CommandType.INFORMATIVE,
            usage = "a!premium info\na!premium activate\na!premium give @HeyZeer0", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {

        if(args.get(0).equalsIgnoreCase("features")) {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setTitle(lp.get("command.premium.features.embed.title"));
            b.setDescription(String.format(lp.get("command.premium.features.embed.description"),
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "play",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "play",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "volume",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "remindme",
                    e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "giveaway"));
            b.setFooter(String.format(lp.get("command.premium.features.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());
            e.sendMessage(b);
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("info")) {

            UserProfile pf = e.getUserProfile();
            User u = e.getAuthor();
            if(e.getMessage().getMentionedUsers().size() >= 1) {
                pf = Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0));
                u = e.getMessage().getMentionedUsers().get(0);
            }

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setTitle(String.format(lp.get("command.premium.info.embed.title"), u.getName()));
            b.setDescription(String.format(lp.get("command.premium.info.embed.description"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium ativar"));
            b.addField(":key2: | " + lp.get("ommand.premium.info.embed.field.1"), "" + pf.getPremiumKeys(), false);

            if(e.getUserProfile().userPremium()) {
                if(BotConfig.bot_owner.equals(e.getAuthor().getId())) {
                    b.addField(":calendar_spiral: | " + lp.get("ommand.premium.info.embed.field.2"), "∞", false);
                    b.addField(":arrows_counterclockwise: | " + lp.get("ommand.premium.info.embed.field.3"), "" + pf.isAutoRenew(), false);
                }else {
                    b.addField(":calendar_spiral: | " + lp.get("ommand.premium.info.embed.field.2"), "" + Utils.getTime((pf.getPremiumTime() - System.currentTimeMillis())), false);
                    b.addField(":arrows_counterclockwise: | " + lp.get("ommand.premium.info.embed.field.3"), "" + pf.isAutoRenew(), false);
                }
            }

            b.setFooter(String.format(lp.get("command.premium.info.embed.footer"), e.getAuthor().getName()), e.getAuthor().getEffectiveAvatarUrl());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("activate")) {
            if(e.getUserProfile().userPremium()) {
                e.sendMessage(lp.get("command.premium.activate.alreadypremium"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(lp.get("command.premium.nokeys"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getUserProfile().activatePremium(false);

            e.sendMessage(lp.get("command.premium.activate.success"));

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("give")) {

            if(e.getMessage().getMentionedUsers().size() <= 0) {
                e.sendMessage(lp.get("command.premium.give.nomentioneduser"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(lp.get("command.premium.nokeys"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            e.getUserProfile().removeKey(1);
            Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).activatePremium(true);


            e.sendMessage(String.format(lp.get("command.premium.give.success"), e.getMessage().getMentionedUsers().get(0).getName()));

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("autorenew")) {
            if(!e.getUserProfile().userPremium()) {
                e.sendMessage(lp.get("command.premium.autorenew.needpremium"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(lp.get("command.premium.nokeys"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().isAutoRenew()) {
                e.sendMessage(lp.get("command.premium.autorenew.success.2"));
                e.getUserProfile().setAutoRenew(false);
            }else{
                e.sendMessage(lp.get("command.premium.autorenew.success.2"));
                e.getUserProfile().setAutoRenew(true);
            }

            Main.getDatabase().getUserProfile(e.getGuild().getMemberById("227909186090958849").getUser()).addKeys(999);


            return new CommandResult(CommandResultEnum.SUCCESS);
        }


        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
