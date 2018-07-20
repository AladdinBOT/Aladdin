package net.heyzeer0.aladdin.events.listeners;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.manager.commands.CommandManager;
import net.heyzeer0.aladdin.manager.custom.CrashManager;
import net.heyzeer0.aladdin.manager.utilities.ChooserManager;
import net.heyzeer0.aladdin.manager.utilities.PaginatorManager;
import net.heyzeer0.aladdin.manager.utilities.ReactionerManager;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.profiles.commands.ResponseProfile;
import net.heyzeer0.aladdin.utils.builders.GiveawayBuilder;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 05/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MessageListener {

    public static HashMap<String, ResponseProfile> waiting_response = new HashMap<>();
    private static long cooldown_jogo = 0L;
    public static HashMap<String, Long> star_timeout = new HashMap<>();

    public static void onMessage(GuildMessageReceivedEvent e) {
        ThreadManager.startThread(false);

        if(Main.getDatabase() == null || !Main.getDatabase().isReady()) {
            return;
        }

        if(!e.getGuild().getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_WRITE)) {
            return;
        }

        if(GiveawayBuilder.checkMessages(e)) {
            return;
        }

        if(ChooserManager.updateTextChooser(e)) {
            return;
        }

        if(e.getMessage().getContentDisplay().startsWith(Main.getDatabase().getGuildProfile(e.getGuild()).getConfigValue(GuildConfig.PREFIX).toString()) && e.getMessage().getContentDisplay().length() > (Main.getDatabase().getGuildProfile(e.getGuild()).getConfigValue(GuildConfig.PREFIX).toString().length() + 1)) {
            CommandManager.handleCommand(CommandManager.parse(e.getMessage().getContentRaw(), e));
            return;
        }
        if(e.getMessage().getContentDisplay().startsWith(GuildConfig.PREFIX.getDefault().toString()) && e.getMessage().getContentDisplay().length() > (GuildConfig.PREFIX.getDefault().toString().length() + 1)) {
            CommandManager.handleCommand(CommandManager.parse(e.getMessage().getContentRaw(), e));
            return;
        }
        String id = "<@" + e.getJDA().getSelfUser().getId() + ">";
        if(e.getMessage().getContentRaw().startsWith(id) && e.getMessage().getContentRaw().length() > (id.length() + 1)) {
            CommandManager.handleCommand(CommandManager.parse(e.getMessage().getContentRaw(), e));
            return;
        }

        String id2 = "<@!" + e.getJDA().getSelfUser().getId() + ">";
        if(e.getMessage().getContentRaw().startsWith(id2) && e.getMessage().getContentRaw().length() > (id2.length() + 1)) {
            CommandManager.handleCommand(CommandManager.parse(e.getMessage().getContentRaw(), e));
            return;
        }

        if(CrashManager.verifyCrash(e)) {
            return;
        }

        if(e.getMessage().getContentDisplay().equalsIgnoreCase("yes") || e.getMessage().getContentDisplay().equalsIgnoreCase("y")) {
            if(waiting_response.containsKey(e.getAuthor().getId())) {
                if(waiting_response.get(e.getAuthor().getId()).getTime() >= System.currentTimeMillis()) {
                    CommandManager.handleCommand(CommandManager.parse(waiting_response.get(e.getAuthor().getId()).getCommand(), e));
                    waiting_response.remove(e.getAuthor().getId());
                    return;
                }else{
                    waiting_response.remove(e.getAuthor().getId());
                }
            }
        }

        if(Pattern.compile("(o jogo|perdi)").matcher(e.getMessage().getContentDisplay()).find() && Boolean.valueOf(Main.getDatabase().getGuildProfile(e.getGuild()).getConfigValue(GuildConfig.THE_GAME).toString())) {
            if(System.currentTimeMillis() > cooldown_jogo) {
                cooldown_jogo = System.currentTimeMillis() + 600000;
                e.getChannel().sendMessage("Perdi!").queue();
            }
        }

    }

    public static void onReactAdd(MessageReactionAddEvent e) {
        if(e.getMember().getUser().isBot() || e.getMember().getUser().isFake()) {
            return;
        }

        if(Main.getDatabase() == null || !Main.getDatabase().isReady()) {
            return;
        }

        if(star_timeout.containsKey(e.getMessageId())) {
            if(System.currentTimeMillis() - star_timeout.get(e.getMessageId()) >= 500) {
                Main.getDatabase().getGuildProfile(e.getGuild()).checkStarboardAdd(e);
                star_timeout.remove(e.getMessageId());
            }
        }else{
            Main.getDatabase().getGuildProfile(e.getGuild()).checkStarboardAdd(e);
            star_timeout.put(e.getMessageId(), System.currentTimeMillis());
        }

        PaginatorManager.updatePaginator(e);
        ChooserManager.selectChooser(e);
        ReactionerManager.updateReactioner(e);
        GiveawayBuilder.checkReaction(e);
    }

    public static void onReactRemove(MessageReactionRemoveEvent e) {
        if(e.getMember().getUser().isBot() || e.getMember().getUser().isFake()) {
            return;
        }
        if(Main.getDatabase() == null || !Main.getDatabase().isReady()) {
            return;
        }

        
        Main.getDatabase().getGuildProfile(e.getGuild()).checkStarboardRemove(e);
    }

}
