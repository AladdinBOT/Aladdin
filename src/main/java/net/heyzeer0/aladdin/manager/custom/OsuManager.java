package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.profiles.custom.OsuPlayerProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONObject;

/**
 * Created by HeyZeer0 on 19/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuManager {

    public static OsuPlayerProfile getUserProfile(String user) throws Exception {
        OsuPlayerProfile profile = new OsuPlayerProfile(user);
        String website = Utils.readWebsite("https://osu.ppy.sh/api/get_user?k=" + ApiKeysConfig.osu_api_key + "&u=" + user);

        JSONObject json = new JSONObject(website.substring(1, website.length()-1));

        profile.userid = json.getString("user_id");
        profile.count300 = json.getString("count300");
        profile.count100 = json.getString("count100");
        profile.count50 = json.getString("count50");
        profile.playcount = json.getString("playcount");
        profile.ranked_score = json.getString("ranked_score");
        profile.total_score = json.getString("total_score");
        profile.pp_rank = json.getString("pp_rank");
        profile.level = json.getString("level");
        profile.accuracy = json.getString("accuracy");
        profile.count_rank_ss = json.getString("count_rank_ss");
        profile.count_rank_s = json.getString("count_rank_s");
        profile.count_rank_a = json.getString("count_rank_a");
        profile.country = json.getString("country");
        profile.country_rank = json.getString("pp_country_rank");
        profile.pp_raw = json.getString("pp_raw");

        profile.exist = true;

        return profile;
    }


}
