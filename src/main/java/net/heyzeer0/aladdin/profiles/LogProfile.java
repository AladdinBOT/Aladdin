package net.heyzeer0.aladdin.profiles;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HeyZeer0 on 05/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class LogProfile {

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    String name;

    public LogProfile(String name) {
        this.name = name;
    }

    public void warn(String msg) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [" + name + "/WARN] " + msg);
    }

    public void info(String msg) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [" + name + "/INFO] " + msg);
    }

}
