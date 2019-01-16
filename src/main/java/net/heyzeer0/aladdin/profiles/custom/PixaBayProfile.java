package net.heyzeer0.aladdin.profiles.custom;


/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
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

    public int getDownloads() {
        return downloads;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public String getPageURL() {
        return pageURL;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }
}
