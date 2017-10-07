package net.heyzeer0.aladdin.profiles.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CommandArgument {

    public final String raw;
    public final String beheaded;
    public final String[] splitBeheaded;
    public final String invoke;
    public final String[] args;
    public final GuildMessageReceivedEvent event;

    public CommandArgument(String raw, String beheaded, String[] splitBeheaded, String invoke, String[] args, GuildMessageReceivedEvent e) {
        this.raw = raw;
        this.beheaded = beheaded;
        this.splitBeheaded = splitBeheaded;
        this.invoke = invoke.toLowerCase();
        this.args = args;
        this.event = e;
    }
}
