package net.heyzeer0.aladdin.manager.custom.osu;

import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuBeatmapProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuPlayerProfile;
import net.heyzeer0.aladdin.utils.Router;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 19/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuManager {

    public static OsuPlayerProfile getUserProfile(String user, boolean id) throws Exception {
        OsuPlayerProfile profile = new OsuPlayerProfile(user);
        String website = new Router("https://osu.ppy.sh/api/get_user?k=" + ApiKeysConfig.osu_api_key + "&u=" + user + (id ? "&type=id" : "")).getResponse().getResult();

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
        profile.count_rank_sh = json.getString("count_rank_sh");
        profile.count_rank_ssh = json.getString("count_rank_ssh");
        profile.nome = json.getString("username");

        profile.exist = true;

        return profile;
    }

    public static ArrayList<OsuMatchProfile> getTop10FromPlayer(String user) throws Exception {
        JSONArray c = new Router("https://osu.ppy.sh/api/get_user_best?k=" + ApiKeysConfig.osu_api_key + "&u=" + user).getResponse().asJsonArray();
        ArrayList<OsuMatchProfile> matches = new ArrayList<>();

        for(int i = 0; i < c.length(); i++) {
            JSONObject obj = c.getJSONObject(i);

            matches.add(
                    new OsuMatchProfile(obj.getString("beatmap_id"), obj.getString("score"), obj.getString("maxcombo"), obj.getString("count300")
                    , obj.getString("count100"), obj.getString("count50"), obj.getString("countmiss"), obj.getString("countkatu"), obj.getString("countgeki")
                    , obj.getString("perfect"), obj.getString("enabled_mods"), obj.getString("user_id"), obj.getString("date"), obj.getString("rank"), obj.getString("pp"))
            );
        }

        Utils.runAsync(() -> {
            for(OsuMatchProfile mm : matches) {
                OppaiManager.getMapInfo(mm.getBeatmap_id(), OsuMods.asString(mm.getMods()));
            }
        });

        return matches;
    }

    public static ArrayList<OsuMatchProfile> getRecentFromPlayer(String user, int amount) throws Exception {
        JSONArray c = new Router("https://osu.ppy.sh/api/get_user_recent?k=" + ApiKeysConfig.osu_api_key + "&limit=" + amount +"&u=" + user).getResponse().asJsonArray();
        ArrayList<OsuMatchProfile> matches = new ArrayList<>();

        for(int i = 0; i < c.length(); i++) {
            JSONObject obj = c.getJSONObject(i);

            matches.add(
                    new OsuMatchProfile(obj.getString("beatmap_id"), obj.getString("score"), obj.getString("maxcombo"), obj.getString("count300")
                            , obj.getString("count100"), obj.getString("count50"), obj.getString("countmiss"), obj.getString("countkatu"), obj.getString("countgeki")
                            , obj.getString("perfect"), obj.getString("enabled_mods"), obj.getString("user_id"), obj.getString("date"), obj.getString("rank"))
            );
        }

        Utils.runAsync(() -> {
            for(OsuMatchProfile mm : matches) {
                OppaiManager.getMapInfo(mm.getBeatmap_id(), OsuMods.asString(mm.getMods()));
            }
        });

        return matches;
    }

    public static ArrayList<OsuMatchProfile> getTop50FromPlayer(String user) throws Exception {
        JSONArray c = new Router("https://osu.ppy.sh/api/get_user_best?k=" + ApiKeysConfig.osu_api_key + "&limit=50&u=" + user).getResponse().asJsonArray();
        ArrayList<OsuMatchProfile> matches = new ArrayList<>();

        for(int i = 0; i < c.length(); i++) {
            JSONObject obj = c.getJSONObject(i);

            matches.add(
                    new OsuMatchProfile(obj.getString("beatmap_id"), obj.getString("score"), obj.getString("maxcombo"), obj.getString("count300")
                            , obj.getString("count100"), obj.getString("count50"), obj.getString("countmiss"), obj.getString("countkatu"), obj.getString("countgeki")
                            , obj.getString("perfect"), obj.getString("enabled_mods"), obj.getString("user_id"), obj.getString("date"), obj.getString("rank"), obj.getString("pp"))
            );
        }

        Utils.runAsync(() -> {
            for(OsuMatchProfile mm : matches) {
                OppaiManager.getMapInfo(mm.getBeatmap_id(), OsuMods.asString(mm.getMods()));
            }
        });

        return matches;
    }

    public static OsuBeatmapProfile getBeatmap(String id) throws Exception {
        String website = new Router("https://osu.ppy.sh/api/get_beatmaps?k=" + ApiKeysConfig.osu_api_key + "&b=" + id).getResponse().getResult();

        JSONObject c = new JSONArray(website).getJSONObject(0);

        return new OsuBeatmapProfile(c.getString("beatmapset_id"),
                c.getString("beatmap_id"),
                c.getString("approved"),
                c.getString("total_length"),
                c.getString("hit_length"),
                c.getString("version"),
                c.getString("file_md5"),
                c.getString("diff_size"),
                c.getString("diff_overall"),
                c.getString("diff_approach"),
                c.getString("diff_drain"),
                c.getString("mode"),
                c.getString("approved_date"),
                c.getString("last_update"),
                c.getString("artist"),
                c.getString("title"),
                c.getString("creator"),
                c.getString("bpm"),
                c.getString("source"),
                c.getString("tags"),
                c.getString("genre_id"),
                c.getString("language_id"),
                c.getString("favourite_count"),
                c.getString("playcount"),
                c.getString("passcount"),
                c.getString("max_combo"),
                c.getString("difficultyrating"));
    }

}
