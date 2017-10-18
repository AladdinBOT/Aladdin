package net.heyzeer0.aladdin.profiles.custom;

import lombok.Getter;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class PixaBayProfile {

    int downloads;
    String webformatURL;
    String pageURL;
    int likes;
    int views;

    public PixaBayProfile(int downloads, String webformatURL, int likes, int views, String pageURL) {
        this.downloads = downloads;
        this.webformatURL = webformatURL;
        this.likes = likes;
        this.views = views;
        this.pageURL = pageURL;
    }

}
