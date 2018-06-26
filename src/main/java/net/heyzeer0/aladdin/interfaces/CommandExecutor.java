package net.heyzeer0.aladdin.interfaces;

import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public interface CommandExecutor {

    CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lang);

}
