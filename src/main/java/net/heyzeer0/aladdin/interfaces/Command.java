package net.heyzeer0.aladdin.interfaces;

import net.heyzeer0.aladdin.enums.CommandType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String command();
    String[] aliasses() default {"none"};
    String description();
    String[] parameters() default {"none"};
    String usage();
    boolean sendTyping() default true;
    String[] extra_perm() default {"none"};
    int deleteCountdown() default 0;
    CommandType type();
    boolean manageWebhooks() default false;
    boolean isAllowedToDefault() default true;
    boolean needPermission() default true;

}
