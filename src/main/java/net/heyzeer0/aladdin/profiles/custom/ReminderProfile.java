package net.heyzeer0.aladdin.profiles.custom;

import lombok.Getter;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class ReminderProfile {

    String reminder;
    long duration;
    String userId;

    @ConstructorProperties({"reminder", "duration", "userId"})
    public ReminderProfile(String reminder, long duration, String userId) {
        this.reminder = reminder;
        this.duration = duration;
        this.userId = userId;
    }

}
