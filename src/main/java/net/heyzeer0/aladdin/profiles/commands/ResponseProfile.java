package net.heyzeer0.aladdin.profiles.commands;
/**
 * Created by HeyZeer0 on 14/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class ResponseProfile {

    long time;
    String command;

    public ResponseProfile(Long waiting, String executed) {
        time = waiting;
        command = executed;
    }

    public long getTime() {
        return time;
    }

    public String getCommand() {
        return command;
    }

}
