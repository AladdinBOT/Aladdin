package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.utils.Router;
import org.json.JSONObject;

/**
 * Created by HeyZeer0 on 10/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class VagalumeManager {

    public static String getLyrics(String artist, String songName) throws Exception {
        JSONObject response = new Router("https://api.vagalume.com.br/search.php?art=" + artist.toLowerCase().replace(" ", "%20") + "&mus=" + songName.toLowerCase().replace(" ", "%20") + "&apikey=" + ApiKeysConfig.vagalume_api_key).getResponse().asJsonObject();

        if(response.has("type") && !response.getString("type").equalsIgnoreCase("exact")) {
            return null;
        }


        return response.getJSONArray("mus").getJSONObject(0).getString("text");
    }

}
