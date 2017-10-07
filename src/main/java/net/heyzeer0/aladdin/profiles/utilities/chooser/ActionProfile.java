package net.heyzeer0.aladdin.profiles.utilities.chooser;

/**
 * Created by HeyZeer0 on 24/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ActionProfile {

    String title;
    Runnable action;

    public ActionProfile(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

}
