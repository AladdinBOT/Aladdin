package net.heyzeer0.aladdin.manager.utilities;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ReactionerManager {

    public static HashMap<String, Reactioner> reactioners = new HashMap<>();

    public static void updateReactioner(MessageReactionAddEvent e) {
        if(reactioners.containsKey(e.getMessageId())) {
            reactioners.get(e.getMessageId()).onReaction(e);
        }
    }

}
