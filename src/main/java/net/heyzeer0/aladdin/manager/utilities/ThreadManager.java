package net.heyzeer0.aladdin.manager.utilities;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.profiles.utilities.ScheduledExecutor;
import net.heyzeer0.aladdin.utils.ConcurrentArrayList;

import java.util.Iterator;

/**
 * Created by HeyZeer0 on 17/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ThreadManager {

    private static Thread th;
    private static ConcurrentArrayList<ScheduledExecutor> runnables = new ConcurrentArrayList<>();

    public static void startThread() {
        if(th != null && th.isAlive()) return;

        Main.getLogger().warn("Starting Runnables Thread");

        th = new Thread(() -> {
            while(true) {
                try{
                    Thread.sleep(1000);
                    if(runnables.size() < 1) {
                        return;
                    }

                    Iterator<ScheduledExecutor> r = runnables.iterator();
                    while(r.hasNext()) {
                        r.next().run();
                    }

                }catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        th.setName("Scheduled Executors");
        th.start();
    }

    public static void registerScheduledExecutor(ScheduledExecutor r) {
        runnables.add(r);

        startThread();
    }



}
