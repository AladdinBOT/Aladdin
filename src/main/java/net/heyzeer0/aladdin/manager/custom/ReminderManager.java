package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.ServerProfile;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;
import net.heyzeer0.aladdin.profiles.utilities.ActiveThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ReminderManager {

    private static ActiveThread checkingThread;

    public static void startChecking() {
        if(checkingThread != null && !checkingThread.isRunning()){
            checkingThread.startRunning();
            return;
        }

        checkingThread = new ActiveThread("Reminders", 60000, () -> {
            ServerProfile pf = Main.getDatabase().getServer();
            if(pf.getReminders().size() > 0) {
                for(ReminderProfile rp : pf.getReminders()) {
                    if(System.currentTimeMillis() > rp.getDuration()) {
                        try{
                            Main.getUserById(rp.getUserId()).openPrivateChannel().queue(ch -> ch.sendMessage(EmojiList.STOPWATCH + " Você pediu para eu te lembrar de ``" + rp.getReminder() + "`` agora ^-^").queue());
                            pf.removeReminder(rp);
                        }catch (Exception ignored) { }
                    }
                }
            }
        }).startRunning();

    }

}
