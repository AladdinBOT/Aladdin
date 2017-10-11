package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class PlaylistTrackProfile {

    String name;
    String duration;
    String url;

    @ConstructorProperties({"name", "duration", "url"})
    public PlaylistTrackProfile(String name, String duration, String url) {
        this.name = name;
        this.duration = duration;
        this.url = url;
    }

}
