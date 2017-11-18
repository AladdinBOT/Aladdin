package net.heyzeer0.aladdin.utils;

import net.heyzeer0.aladdin.profiles.custom.GoogleSearch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 24/11/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class GoogleUtils {

    public static String searchString = "https://www.youtube.com/results?search_query=";
    public static String result_string = "http://www.youtube.com";

    public static List<String> search_videos(String titulo) {
        List<String> result = new ArrayList<>();

        try {
            Document d = Jsoup.connect(searchString + titulo.replace(" ", "+")).get();

            Elements b = d.body().getElementsByTag("div");
            for(Element b2 : b) {
                b2.getElementsByClass("yt-lockup-dismissable").stream().filter(a -> !result.contains(result_string + a.getElementsByTag("a").attr("href"))).forEach(a -> {
                    result.add(result_string + a.getElementsByTag("a").attr("href"));
                });
            }

        }catch(IOException e){
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static String search_image(String titulo) {
        try{
            Elements x = Jsoup.connect("https://www.google.com/search?ie=ISO-8859-1&hl=en&source=hp&tbm=isch&gbv=1&gs_l=img&q=" + titulo).userAgent("Aladdin-BOT").get().getElementsByTag("img");
            for(Element y : x) {
                return y.absUrl("src");
            }
        }catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static List<GoogleSearch> search_google(String titulo) {
        List<GoogleSearch> list = new ArrayList<>();
        try {

            Elements links = Jsoup.connect(
                    String.format("http://www.google.com/search?q=%s", URLEncoder.encode(titulo, StandardCharsets.UTF_8.displayName())))
                    .userAgent("Aladdin-BOT").get().select(".g>.r>a");

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), StandardCharsets.UTF_8.displayName());

                if (!url.startsWith("http")) {
                    continue;
                }

                list.add(new GoogleSearch(title, url));
            }
        }catch (Exception e) {
            return null;
        }
        return list;
    }

}
