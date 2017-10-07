package net.heyzeer0.aladdin.manager.custom.warframe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.heyzeer0.aladdin.profiles.custom.warframe.WikiProfile;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by HeyZeer0 on 05/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WikiManager {

    public static final String api_url = "http://warframe.wikia.com/api/v1/Articles/Details/?titles=%s&abstract=500";

    public static WikiProfile getWikiArticle(String name) {

        try {
            URL url = new URL(String.format(api_url, name.replace(" ", "%20")));
            InputStream in = url.openStream();
            Scanner scan = new Scanner(in);
            String jsonstring = "";
            while(scan.hasNext()){
                jsonstring += scan.next() + " ";
            }
            scan.close();

            Gson gson = new GsonBuilder().create();

            JsonObject json = gson.fromJson(jsonstring, JsonElement.class).getAsJsonObject();

            for(Map.Entry<String, JsonElement> entry : json.get("items").getAsJsonObject().entrySet()) {
                return new WikiProfile(
                        entry.getValue().getAsJsonObject().get("title").getAsString(),
                        entry.getValue().getAsJsonObject().get("id").getAsString(),
                        entry.getValue().getAsJsonObject().get("abstract").getAsString(),
                        (entry.getValue().getAsJsonObject().get("thumbnail").isJsonNull() ? "http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147" : entry.getValue().getAsJsonObject().get("thumbnail").getAsString().split("/revision")[0]));
            }

        }catch (Exception e) {
            return null;
        }

        return null;
    }


}
