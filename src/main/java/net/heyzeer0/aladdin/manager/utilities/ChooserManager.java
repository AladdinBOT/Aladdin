package net.heyzeer0.aladdin.manager.utilities;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.profiles.utilities.chooser.Chooser;
import net.heyzeer0.aladdin.profiles.utilities.chooser.TextChooser;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HeyZeer0 on 24/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ChooserManager {


    public static HashMap<String, Chooser> choosers = new HashMap<>();
    public static HashMap<String, TextChooser> textchooser = new HashMap<>();
    private static Timer tr = new Timer("Chooser");

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
        tr.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(choosers.size() >= 1) {
                            for(String ks : choosers.keySet()) {
                                choosers.get(ks).clear();
                            }
                        }
                        startCleanup();
                        this.cancel();
                    }
        }, 15000);
    }

}
