package net.heyzeer0.aladdin.profiles.custom;

/**
 * Created by HeyZeer0 on 04/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class GoogleSearch {

    String title;
    String url;

    public GoogleSearch(String titulo, String link) {
        title = titulo;
        url = link;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
