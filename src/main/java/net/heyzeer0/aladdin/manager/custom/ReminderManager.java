package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.ServerProfile;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;
import net.heyzeer0.aladdin.profiles.utilities.ScheduledExecutor;

import java.util.Iterator;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ReminderManager {

    public static void startChecking() {
        ThreadManager.registerScheduledExecutor(new ScheduledExecutor(60000, () -> {
            ServerProfile pf = Main.getDatabase().getServer();
            if(pf.getReminders().size() > 0) {
                Iterator<ReminderProfile> reminders = pf.getReminders().iterator();
                ReminderProfile rp;
                while(reminders.hasNext() && (rp = reminders.next()) != null) {
                    if(System.currentTimeMillis() > rp.getDuration()) {
                        try{
                            String reminder = rp.getReminder();
                            Main.getUserById(rp.getUserId()).openPrivateChannel().queue(ch -> ch.sendMessage(EmojiList.STOPWATCH + " You told me to remember you of ``" + reminder + "`` right now ^-^").queue());
                            pf.removeReminder(rp);
                        }catch (Exception ignored) { }
                    }
                }
            }
        }));
    }

}
