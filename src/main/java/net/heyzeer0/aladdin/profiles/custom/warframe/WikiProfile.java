package net.heyzeer0.aladdin.profiles.custom.warframe;

/**
 * Created by HeyZeer0 on 05/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WikiProfile {

    String name;
    String id;
    String description;
    String thumbnail;

    public WikiProfile(String name, String id, String description, String thumbnail) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean hasThumbnail() {
        return thumbnail != null;
    }

}
