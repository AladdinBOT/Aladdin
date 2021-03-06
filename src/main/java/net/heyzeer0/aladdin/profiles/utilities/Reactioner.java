package net.heyzeer0.aladdin.profiles.utilities;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.manager.utilities.ReactionerManager;

/**
 * Created by HeyZeer0 on 11/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Reactioner {

    TextChannel ch;
    long author;
    ReactionResult result;

    Message msg;

    public Reactioner(String message, long author, TextChannel ch, ReactionResult result) {
        this.ch = ch;
        this.author = author;
        this.result = result;

        msg = ch.sendMessage(message).complete();
        if(msg != null) {
            ReactionerManager.reactioners.put(msg.getId(), this);
        }
    }

    public Reactioner(Message msg, long author, TextChannel ch, ReactionResult result) {
        this.ch = ch;
        this.author = author;
        this.result = result;
        this.msg = msg;

        if(msg != null) {
            ReactionerManager.reactioners.put(msg.getId(), this);
        }
    }

    public void onReaction(MessageReactionAddEvent e) {
        if(e.getMessageIdLong() == msg.getIdLong()) {
            if(e.getUser().getIdLong() == author) {
                result.onReactionAdd(e);
                ReactionerManager.reactioners.remove(msg.getId());
            }
        }
    }

    public interface ReactionResult {
        void onReactionAdd(MessageReactionAddEvent e);
    }

}
