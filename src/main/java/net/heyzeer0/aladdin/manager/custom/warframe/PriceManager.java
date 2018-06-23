package net.heyzeer0.aladdin.manager.custom.warframe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.heyzeer0.aladdin.profiles.custom.warframe.PriceProfile;
import net.heyzeer0.aladdin.utils.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 06/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PriceManager {

    public static String api_url = "https://www.nexus-stats.com/api";

    public static List<PriceProfile> getPrices(String item) throws Exception {
        List<PriceProfile> values = new ArrayList<>();


        Gson gson = new GsonBuilder().create();
        JsonArray json = gson.fromJson(new Router(api_url).getResponse().getResult(), JsonElement.class).getAsJsonArray();

        for(JsonElement entry : json) {
            if(entry.getAsJsonObject().get("Title").getAsString().equalsIgnoreCase(item)) {
                for(JsonElement b : entry.getAsJsonObject().getAsJsonObject().get("Components").getAsJsonArray()) {
                    values.add(new PriceProfile(b.getAsJsonObject().get("name").getAsString(), b.getAsJsonObject().get("avg").getAsString()));
                }
                return values;
            }
        }

        return values;
    }

}
