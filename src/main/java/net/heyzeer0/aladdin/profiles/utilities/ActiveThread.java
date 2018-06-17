package net.heyzeer0.aladdin.profiles.utilities;

/**
 * Created by HeyZeer0 on 17/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ActiveThread {

    Runnable r;
    long delay;
    String name;

    boolean running = false;
    Thread th;
    long lastTick = 0;

    public ActiveThread(String name,  long delay, Runnable r) {
        this.r = r; this.delay = delay; this.name = name;
    }

    public ActiveThread startRunning() {
        if(running && th.isAlive()) {
            return this;
        }
        running = true;
        th = new Thread(() -> {
            while(running) {
                if(System.currentTimeMillis() >= lastTick) {
                    r.run();
                    lastTick = System.currentTimeMillis() + delay;
                }
            }
        });

        th.setName(name);
        th.start();

        return this;
    }

    public void stopThread() {
        running = false;
    }

    public boolean isRunning() {
        return running && th.isAlive();
    }

}
