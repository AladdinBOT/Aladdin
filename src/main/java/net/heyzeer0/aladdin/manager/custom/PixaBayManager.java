package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.profiles.custom.PixaBayProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 18/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PixaBayManager {

    public static List<PixaBayProfile> getImages(String image) throws Exception {
        List<PixaBayProfile> images = new ArrayList<>();


        JSONObject obj_main = new JSONObject(Utils.readWebsite(
                "https://pixabay.com/api/?key=" + ApiKeysConfig.pixabay_api_key + "&q=" + image + "&image_type=photo&per_page=3&safesearch=true"
        ));

        if(obj_main.getInt("total") <= 0) {
            return null;
        }

        JSONArray obj = obj_main.getJSONArray("hits");

        for(int i = 0; i < obj.length(); i++) {
            JSONObject ob = obj.getJSONObject(i);
            images.add(new PixaBayProfile(ob.getInt("downloads"), ob.getString("webformatURL"), ob.getInt("likes"), ob.getInt("views"), ob.getString("pageURL")));
        }

        return images;
    }

}
