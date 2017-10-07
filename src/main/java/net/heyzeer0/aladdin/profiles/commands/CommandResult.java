package net.heyzeer0.aladdin.profiles.commands;

import net.heyzeer0.aladdin.enums.CommandResultEnum;

/**
 * Created by HeyZeer0 on 06/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CommandResult {

    String[] msg;
    CommandResultEnum result;

    public CommandResult(CommandResultEnum result, String... msgs) {
        this.msg = msgs;
        this.result = result;
    }

    public CommandResult(CommandResultEnum result) {
        this.result = result;
    }

    public CommandResultEnum getResult() {
        return result;
    }

    public String[] getMessage() {
        return msg;
    }

}
