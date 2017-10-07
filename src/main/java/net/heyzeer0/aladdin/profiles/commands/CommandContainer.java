package net.heyzeer0.aladdin.profiles.commands;

import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CommandContainer {

    CommandExecutor exec;
    Command annotation;

    public CommandContainer(CommandExecutor e, Command a) {
        exec = e;
        annotation = a;
    }

    public CommandExecutor getExecutor() {
        return exec;
    }

    public Command getAnnotation() {
        return annotation;
    }

}
