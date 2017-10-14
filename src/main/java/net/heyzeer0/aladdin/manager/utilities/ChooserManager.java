package net.heyzeer0.aladdin.manager.utilities;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.profiles.utilities.chooser.Chooser;
import net.heyzeer0.aladdin.profiles.utilities.chooser.TextChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 24/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ChooserManager {


    public static HashMap<String, Chooser> choosers = new HashMap<>();
    public static HashMap<String, TextChooser> textchooser = new HashMap<>();
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static void registerChooser(Chooser ch) {
        choosers.put(ch.selector.getId(), ch);
    }

    public static void selectChooser(MessageReactionAddEvent e) {
        if(choosers.containsKey(e.getMessageId())) {
            Chooser ch = choosers.get(e.getMessageId());
            ch.clickAction(e);
        }
    }

    public static boolean updateTextChooser(GuildMessageReceivedEvent e) {
        if(textchooser.containsKey(e.getAuthor().getId())) {
            return textchooser.get(e.getAuthor().getId()).makeChoice(e);
        }
        return false;
    }

    public static void startCleanup() {
        service.scheduleAtFixedRate(() -> {
            if(choosers.size() >= 1) {
                List<String> to_delete = new ArrayList<>();
                for(String ks : choosers.keySet()) {
                    if(System.currentTimeMillis() - choosers.get(ks).getLastAction() >= 15000) {
                        to_delete.add(ks);
                    }
                }

                if(to_delete.size() >= 1)
                    to_delete.forEach(str -> choosers.get(str).clear());
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

}
