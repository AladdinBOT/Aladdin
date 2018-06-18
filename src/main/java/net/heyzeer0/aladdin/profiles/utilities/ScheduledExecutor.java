package net.heyzeer0.aladdin.profiles.utilities;

/**
 * Created by HeyZeer0 on 17/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ScheduledExecutor {

    long delay;
    Runnable r;

    long last_activity = 0;

    public ScheduledExecutor(long delay, Runnable r) {
        this.delay = delay; this.r = r;
    }

    public void run() {
        if(System.currentTimeMillis() >= last_activity) {
            last_activity = System.currentTimeMillis() + delay;
            r.run();
        }
    }

}
