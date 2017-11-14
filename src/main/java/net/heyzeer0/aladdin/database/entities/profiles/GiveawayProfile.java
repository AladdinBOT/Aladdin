package net.heyzeer0.aladdin.database.entities.profiles;

import lombok.Getter;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 14/11/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class GiveawayProfile {

    String messageID;
    String channelID;
    String guildID;
    String description;
    String authorName;
    String authorAvatar;
    long endTime;
    int winnerAmount;

    @ConstructorProperties({"messageID", "channelID", "guildID", "description", "authorName", "authorAvatar", "endTime", "winnerAmount"})
    public GiveawayProfile(String messageID, String channelID, String guildID, String description, String authorName, String authorAvatar, long endTime, int winnerAmount) {
        this.messageID = messageID; this.channelID = channelID; this.guildID = guildID; this.description = description; this.authorName = authorName; this.authorAvatar = authorAvatar; this.endTime = endTime; this.winnerAmount = winnerAmount;
    }

}
