package net.heyzeer0.aladdin.enums;

/**
 * Created by HeyZeer0 on 06/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum GuildConfig {

    PREFIX("a!"),
    MEMBER_CREATE_CMDS(true),
    MEMBER_CAN_REPEAT(true),
    MINECRAFT_CRASHHELPER(false),
    THE_GAME(false),
    STARBOARD_SELFREACT(true);

    Object df;

    GuildConfig(Object x) {
        df = x;
    }

    public Object getDefault() {
        return df;
    }

}
