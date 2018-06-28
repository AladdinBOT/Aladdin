package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.database.entities.profiles.StarboardProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class StarboardCommand implements CommandExecutor {

    @Command(command = "starboard", description = "command.starboard.description", aliasses = {"sboard"}, parameters = {"create/config/remove/list"}, type = CommandType.MISCELLANEOUS, isAllowedToDefault = false,
            usage = "a!starboard create 3 #starboard\na!starboard config 0 info\na!starboard config 0 setamount 3\na!starboard config 0 ignorechannel #testes\na!starboard remove 0\na!starboard list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("create")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "create", lp.get("command.starboard.create.arg.1"), lp.get("command.starboard.create.arg.2"));
            }

            if(e.getMessage().getMentionedChannels().size() < 1) {
                e.sendMessage(lp.get("command.starboard.create.error.nochannel"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            try{

                Integer amount = Integer.valueOf(args.get(1));
                String ch = e.getMessage().getMentionedChannels().get(0).getId();

                new Reactioner(lp.get("command.starboard.create.reactioner.title"), e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                    String emote = v.getReactionEmote().getName() + "|" + (v.getReactionEmote().getId() == null ? "null" : v.getReactionEmote().getId());
                    if(e.getGuildProfile().getGuild_starboards().containsKey(emote)) {
                        e.sendMessage(lp.get("command.starboard.create.reactioner.invalidemote"));
                        return;
                    }
                    e.getGuildProfile().createStarboard(emote, amount, ch);

                    e.sendMessage(lp.get("command.starboard.create.success"));
                });

            }catch (Exception ex) {
                e.sendMessage(lp.get("command.starboard.create.error.invalidamount"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("config")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "setamount/ignorechannel/info");
            }

            try{
                Integer id = Integer.valueOf(args.get(1));

                if(id < 0) {
                    e.sendMessage(lp.get("command.starboard.config.error.invalidnumber"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(e.getGuildProfile().getGuild_starboards().size() < id) {
                    e.sendMessage(lp.get("command.starboard.config.error.invalidstarboard"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("info")) {
                    StarboardProfile pf = e.getGuildProfile().getStarboardById(id);

                    Paginator ph = new Paginator(e, String.format(lp.get("command.starboard.config.info.paginator.title"), id));
                    ph.addPage(String.format(lp.get("command.starboard.config.info.paginator.page.1"), pf.getEmote().split("\\|")[0], pf.getAmount(), pf.getMessages().size()));

                    String x = lp.get("command.starboard.config.info.paginator.page.2") + "\n";

                    if(pf.getBlocked_channels().size() >= 1) {
                        for(String k : pf.getBlocked_channels().keySet()) {
                            x = x + "#" + pf.getBlocked_channels().get(k) + " (" + k + ")\n";
                        }

                        ph.addPage(x);
                    }else{
                        ph.addPage(lp.get("command.starboard.config.info.paginator.page.3", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "starboard config " + id + " ignorechannel #canal"));
                    }

                    ph.start();
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("setamount")) {
                    if(args.getSize() < 4) {
                        return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "setamount", "quantidade");
                    }

                    try{

                        Integer amount = Integer.valueOf(args.get(3));

                        if(amount <= 0) {
                            e.sendMessage(lp.get("command.starboard.config.setamount.error.invalidamount"));
                            return new CommandResult(CommandResultEnum.SUCCESS);
                        }

                        e.getGuildProfile().changeStarboardAmount(id, amount);
                        e.sendMessage(lp.get("command.starboard.config.setamount.success", id, amount));
                    }catch (Exception ex) {
                        e.sendMessage(lp.get("command.starboard.config.setamount.error.invalidamount"));
                    }

                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("ignorechannel")) {
                    if(args.getSize() < 4) {
                        return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "ignorechannel", "#canal");
                    }

                    if(e.getMessage().getMentionedChannels().size() <= 0) {
                        e.sendMessage(lp.get("command.starboard.config.ignorechannel.error.nochannel"));
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }

                    if(e.getGuildProfile().isBlockedChannel(id, e.getMessage().getMentionedChannels().get(0))) {
                        e.getGuildProfile().removeBlockedChannelToStarboard(e.getMessage().getMentionedChannels().get(0), id);

                        e.sendMessage(lp.get("command.starboard.config.ignorechannel.success.1", e.getMessage().getMentionedChannels().get(0).getAsMention(), id));
                    }else{
                        e.getGuildProfile().addBlockedChannelToStarboard(e.getMessage().getMentionedChannels().get(0), id);

                        e.sendMessage(lp.get("command.starboard.config.ignorechannel.success.2", e.getMessage().getMentionedChannels().get(0).getAsMention(), id));
                    }

                    return new CommandResult(CommandResultEnum.SUCCESS);
                }


                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "amount/ignorechannel");
            }catch (Exception ex) {
                e.sendMessage(lp.get("command.starboard.config.error.invalidstarboard"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remove")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "deletar", "id");
            }

            try{
                Integer id = Integer.valueOf(args.get(1));

                if(id < 0) {
                    e.sendMessage(lp.get("command.starboard.remove.error.invalidamount"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(e.getGuildProfile().getGuild_starboards().size() < id) {
                    e.sendMessage(lp.get("command.starboard.remove.error.invalidstarboard"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                e.getGuildProfile().deleteStarboard(id);

                e.sendMessage(lp.get("command.starboard.remove.success", id));

            }catch (Exception ex) {
                e.sendMessage(lp.get("command.starboard.remove.error.invalidamount"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            HashMap<String, StarboardProfile> starboards = e.getGuildProfile().getGuild_starboards();
            String[] keyset = starboards.keySet().toArray(new String[] {});

            if(starboards.size() <= 0) {
                e.sendMessage(lp.get("command.starboard.list.error.nostarboards", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "starboard create"));
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Paginator ph = new Paginator(e, lp.get("command.starboard.list.paginator.title"));

            int pages = (starboards.size() + (10 + 1)) / 10;

            Integer actual = 0;
            Integer pactual = 1;


            for (int i = 1; i <= pages; i++) {
                String pg = "";
                for (int p = actual; p < pactual * 10; p++) {
                    if (starboards.size() <= p) {
                        break;
                    }
                    actual++;

                    String emote = starboards.get(keyset[p]).getEmote().split("\\|")[0];

                    pg = pg + lp.get("command.starboard.list.paginator.page", p, emote, starboards.get(keyset[p]).getMessages().size()) + "\n";
                }
                pactual++;
                ph.addPage(pg);

            }

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
