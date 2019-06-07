/*
 * Developed by HeyZeer0 on 6/7/19 3:47 PM.
 * Last Modification 6/7/19 3:47 PM.
 *
 * Copyright HeyZeer0 (c) 2019.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.manager.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.events.listeners.MessageListener;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.permissions.NodeManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.*;
import net.heyzeer0.aladdin.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandManager {

    public static HashMap<String, CommandContainer> commands = new HashMap<>();
    public static HashMap<String, CommandContainer> aliases = new HashMap<>();

    public static Integer comandos_executados = 0;

    public static void registerCommand(CommandExecutor e) {
        for (Method method : e.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            if(annotation == null) {
                continue;
            }

            commands.put(annotation.command().toLowerCase(), new CommandContainer(e, annotation));

            if(annotation.needPermission()) {
                NodeManager.getNode("command").addSubnode("command." + annotation.command());
                if (!annotation.extra_perm()[0].equalsIgnoreCase("none")) {
                    for (String x : annotation.extra_perm()) {
                        NodeManager.getNode("command").addSubnode("command." + annotation.command() + "." + x);
                    }
                }
            }

            if(annotation.aliasses()[0] == null || annotation.aliasses()[0].equalsIgnoreCase("none")) {
                continue;
            }

            for(int i = 0; i < annotation.aliasses().length; i++) {
                aliases.put(annotation.aliasses()[i], new CommandContainer(e, annotation));
            }
        }
    }

    public static void handleCommand(CommandArgument arg) {
        MessageEvent e = new MessageEvent(arg.event);

        LangProfile lp = Main.getDatabase().getGuildProfile(e.getGuild()).getSelectedLanguage().getLangProfile();

        if(commands.containsKey(arg.invoke)) {

            if(e.getAuthor().isFake()) {
                if(e.getChannelType() == ChannelType.TEXT) {
                    e.sendMessage(lp.get("commandexecutor.fake"));
                }
                return;
            }

            CommandContainer cmd = commands.get(arg.invoke);

            if(cmd.getAnnotation().manageWebhooks()) {
                if(!e.getGuild().getSelfMember().hasPermission(e.getOriginEvent().getChannel(), Permission.MANAGE_WEBHOOKS)) {
                    e.sendMessage(lp.get("commandexecutor.managewebhooks"));
                    return;
                }
            }

            if(cmd.getAnnotation().type() == CommandType.BOT_ADMIN) {
                if(!e.getAuthor().getId().equals(BotConfig.bot_owner)) {
                    e.sendMessage(String.format(lp.get("commandexecutor.nopermission"), "BOT_OWNER"));
                    return;
                }
            }

            if(cmd.getAnnotation().needPermission() && !e.hasPermission("command." + cmd.getAnnotation().command())) {
                e.sendMessage(String.format(lp.get("commandexecutor.nopermission"), "command." + cmd.getAnnotation().command()));
                return;
            }

            if(!cmd.getAnnotation().parameters()[0].equalsIgnoreCase("none")) {
                if(arg.args.length < cmd.getAnnotation().parameters().length) {

                    List<String> params = new ArrayList<>();

                    for(int i = 0; i < cmd.getAnnotation().parameters().length; i++) {
                        params.add("[" + cmd.getAnnotation().parameters()[i] + "]");
                    }

                    e.sendMessage(String.format(lp.get("commandexecutor.invalidparams"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + arg.invoke, StringUtils.join(params, " ")));
                    return;
                }
            }

            if(cmd.getAnnotation().sendTyping()) {
                e.getOriginEvent().getChannel().sendTyping().complete();
            }

            comandos_executados++;
            try{
                CommandResult r = cmd.getExecutor().onCommand(new ArgumentProfile(arg.args), e, lp);

                if(r.getResult() == CommandResultEnum.NOT_FOUND) {
                    List<String> params = new ArrayList<>();

                    for(int i = 0; i < cmd.getAnnotation().parameters().length; i++) {
                        params.add("[" + cmd.getAnnotation().parameters()[i] + "]");
                    }

                    e.sendMessage(String.format(lp.get("commandexecutor.invalidparams"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + arg.invoke, StringUtils.join(params, " ")));
                    return;
                }

                if(r.getResult() == CommandResultEnum.NEED_PREMIUM) {
                    e.sendMessage(String.format(lp.get("commandexecutor.premium"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium features"));
                    return;
                }

                if(r.getResult() == CommandResultEnum.MISSING_ARGUMENT) {
                    List<String> params = new ArrayList<>();

                    for(int i = 1; i < r.getMessage().length; i++) {
                        params.add("[" + r.getMessage()[i] + "]");
                    }

                    e.sendMessage(String.format(lp.get("commandexecutor.invalidparams"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + arg.invoke + " " + r.getMessage()[0], StringUtils.join(params, " ")));
                    return;
                }

                if(r.getResult() == CommandResultEnum.MISSING_PERMISSION) {
                    e.sendMessage(String.format(lp.get("commandexecutor.nopermission"), r.getMessage()[0]));
                    return;
                }

                if(cmd.getAnnotation().deleteCountdown() >= 1) {
                    Utils.runLater(e::deleteMessages, cmd.getAnnotation().deleteCountdown(), TimeUnit.SECONDS);
                }

            }catch (Exception ex) {
                e.sendMessage(lp.get("commandexecutor.error"));
                ex.printStackTrace();

                e.getJDA().getUserById("169904764048375809").openPrivateChannel().queue(ch -> ch.sendMessage("Erro capturado: ```" + Utils.sendToHastebin(Utils.getStackTrace(ex)) + "```").queue());
            }
            return;
        }
        if(aliases.containsKey(arg.invoke)) {
            CommandArgument b = new CommandArgument(arg.raw, arg.beheaded, aliases.get(arg.invoke).getAnnotation().command(), arg.args, arg.event);
            handleCommand(b);
            return;
        }

        if(e.getGuildProfile().hasCustomCommand(arg.invoke)) {
            String msg = e.getGuildProfile().getCustomCommand(arg.invoke).handleCommand(e, arg.args);
            if(msg != null && !msg.contains("@everyone") && !msg.contains("@here")) e.sendMessage(msg);
            return;
        }

        for(String cmd : commands.keySet()) {
            if(StringUtils.getLevenshteinDistance(arg.raw.contains(" ") ? arg.raw.split(" ")[0] : arg.raw, cmd) <= 4) {
                if(e.hasPermission("command." + commands.get(cmd).getAnnotation().command())) {
                    e.sendMessage(String.format(lp.get("commandexecutor.suppostcommand"), e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + cmd));
                    MessageListener.waiting_response.put(e.getAuthor().getId(), new ResponseProfile(System.currentTimeMillis() + 5000l, arg.raw.replace(arg.raw.contains(" ") ? arg.raw.split(" ")[0] : arg.raw, cmd)));
                    break;
                }
            }
        }

    }

    public static CommandArgument parse(String rw, GuildMessageReceivedEvent e) {
        String beheaded = rw.replaceFirst(Main.getDatabase().getGuildProfile(e.getGuild()).getConfigValue(GuildConfig.PREFIX).toString(), "");
        beheaded = beheaded.replaceFirst(GuildConfig.PREFIX.getDefault().toString(), "");
        beheaded = beheaded.replaceFirst("<@" + e.getJDA().getSelfUser().getId() + "> ", "");
        beheaded = beheaded.replaceFirst("<@!" + e.getJDA().getSelfUser().getId() + "> ", "");

        ArrayList<String> split = new ArrayList<>();

        boolean searching = false;
        String to_add = "";
        for(String x : beheaded.split(" ")) {
            if(x.endsWith("\"") && searching) {
                searching = false;
                to_add = to_add + x.replace("\"", "");
                split.add(to_add);
                to_add = "";
                continue;
            }

            if(searching) {
                to_add = to_add + x + " ";
                continue;
            }

            if(x.startsWith("\"")) {
                if(x.startsWith("/\"")) {
                    split.add(x.replace("/", ""));
                    continue;
                }
                to_add = x.replace("\"", "") + " ";
                searching = true;
                continue;
            }

            split.add(x);
        }

        String invoke = split.get(0);
        String[] args = new String[split.size() -1];
        split.subList(1, split.size()).toArray(args);
        return new CommandArgument(rw, beheaded, invoke, args, e);
    }

}
