package net.heyzeer0.aladdin.database.entities.profiles;

import lombok.Getter;
import net.heyzeer0.aladdin.utils.builders.Prize;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Created by HeyZeer0 on 14/11/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class GiveawayProfile {

    String messageID;
    String channelID;
    String guildID;
    String authorName;
    String authorAvatar;
    String title;
    List<Prize> prizes;
    long endTime;

    @ConstructorProperties({"messageID", "channelID", "guildID", "authorName", "authorAvatar", "title", "prizes", "endTime"})
    public GiveawayProfile(String messageID, String channelID, String guildID, String authorName, String authorAvatar, String title, List<Prize> prizes, long endTime) {
        this.messageID = messageID; this.channelID = channelID; this.guildID = guildID; this.authorName = authorName; this.authorAvatar = authorAvatar; this.title = title; this.prizes = prizes; this.endTime = endTime;
    }

    public void endNow() {
        endTime = System.currentTimeMillis() + 60000;
    }

}
