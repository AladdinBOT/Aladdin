package net.heyzeer0.aladdin.profiles.utilities.chooser;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.manager.utilities.ChooserManager;
import net.heyzeer0.aladdin.utils.Utils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 24/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Chooser {

    String title;

    public Message selector;
    Integer options = 0;

    HashMap<Integer, ActionProfile> actions = new HashMap<>();

    long start = 0;

    Message e;

    boolean selected = false;

    public Chooser(Message e, String title) {
        this.title = title;
        this.e = e;
    }

    public void addOption(String title, Runnable r) {
        if(options + 1 > 9) {
            return;
        }
        options++;
        actions.put(options, new ActionProfile(title, r));
    }

    public void clickAction(MessageReactionAddEvent e) {
        if(start == 0) {
            return;
        }
        if(e.getMember().getUser().isFake() || e.getMember().getUser().isBot()) {
            return;
        }
        if(Utils.getRegional(e.getReactionEmote().getName()) != null) {
            if(NumberUtils.isCreatable(Utils.getRegional(e.getReactionEmote().getName()))) {
                try{
                    selected = true;
                    actions.get(Integer.valueOf(Utils.getRegional(e.getReactionEmote().getName()))).action.run();
                    ChooserManager.choosers.remove(selector.getId());
                    selector.delete().queue(scs -> {}, flr -> {});
                }catch (Exception ex) {
                    selector.delete().queue(scs -> {}, flr -> {});
                }
            }
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void start() {
        start = System.currentTimeMillis();

        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);
        b.setTitle(title);

        String description = "";

        for(int i = 1; i <= options; i++) {
            description = description + Utils.getRegional(String.valueOf(i)) + " " + actions.get(i).title + "\n";
        }

        b.setDescription(description);

        Message msg = e.getChannel().sendMessage(new MessageBuilder().setEmbed(b.build()).build()).complete();

        if(msg != null) {
            selector = msg;
            for(int i = 1; i <= options; i++) {
                msg.addReaction(Utils.getRegional(String.valueOf(i))).queue();
            }
            ChooserManager.registerChooser(this);
        }

    }


    public void clear() {
        if(start != 0)
            if(System.currentTimeMillis() - start >= 15000) {
                selected = true;
                ChooserManager.choosers.remove(selector.getId());
                selector.delete().queue();
        }
    }

}
