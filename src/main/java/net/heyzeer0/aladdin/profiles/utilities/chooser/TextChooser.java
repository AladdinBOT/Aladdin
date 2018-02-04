package net.heyzeer0.aladdin.profiles.utilities.chooser;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.heyzeer0.aladdin.manager.utilities.ChooserManager;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 26/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class TextChooser {

    String title;
    MessageEvent e;

    HashMap<Integer, ActionProfile> options = new HashMap<>();
    Integer op = 0;

    long start = 0;

    public TextChooser(MessageEvent e, String title) {
        this.title = title;
        this.e = e;
    }

    public void addOption(String text, Runnable rb) {
        op++;
        options.put(op, new ActionProfile(text, rb));
    }

    public void start() {
        if(ChooserManager.textchooser.containsKey(e.getAuthor().getId())) {
            return;
        }
        EmbedBuilder b = new EmbedBuilder().setColor(Color.GREEN).setTitle(title).setFooter("Digite o número no chat para escolher", e.getJDA().getSelfUser().getAvatarUrl());

        String desc = "``";

        for(Integer k : options.keySet()) {
            desc = desc + "#" + k + " " + options.get(k).title + "\n";
        }

        b.setDescription(desc + "``");

        e.sendMessage(b);
        start = System.currentTimeMillis();

        ChooserManager.textchooser.put(e.getAuthor().getId(), this);
    }

    public boolean makeChoice(GuildMessageReceivedEvent evt) {
        if(!evt.getAuthor().getId().equals(e.getAuthor().getId())) {
            return false;
        }

        if(NumberUtils.isCreatable(evt.getMessage().getContentRaw())) {
            if(options.keySet().contains(Integer.valueOf(evt.getMessage().getContentRaw()))) {
                options.get(Integer.valueOf(evt.getMessage().getContentRaw())).action.run();
                ChooserManager.textchooser.remove(e.getMessage().getAuthor().getId());
            }
        }

        return true;
    }

    public void clear() {
        if(start != 0) {
            if (System.currentTimeMillis() - start >= 7000) {
                ChooserManager.textchooser.remove(e.getMessage().getAuthor().getId());
            }
        }
    }

}
