package net.heyzeer0.aladdin.database.entities.profiles;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.beans.ConstructorProperties;
import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class FStarboardProfile {

    String emote;
    int amount;
    String channel_id;

    HashMap<String, String> messages = new HashMap<>();

    public FStarboardProfile(String emote, int amount, String channel_id) {
        this(emote, amount, channel_id, new HashMap<>());
    }

    @ConstructorProperties({"emote", "amount", "channel_id", "messages"})
    public FStarboardProfile(String emote, int amount, String channel_id, HashMap<String, String> messages) {
        this.emote = emote;
        this.amount = amount;
        this.channel_id = channel_id;
        this.messages = messages;
    }

}
