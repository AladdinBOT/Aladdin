package net.heyzeer0.aladdin.manager.custom.osu;

import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OppaiManager {

    public static OppaiInfo getMapInfo(int map_id) throws Exception {
        Process p = Runtime.getRuntime().exec("./oppai.sh " + map_id + " -ojson");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        JSONObject jo = new JSONObject(br.readLine());

        p.waitFor();
        p.destroy();

        return new OppaiInfo(jo.getString("oppai_version"), jo.getInt("code"), jo.getString("errstr"), jo.getString("artist"), jo.getString("artist_unicode"), jo.getString("title"), jo.getString("title_unicode"), jo.getString("creator"), jo.getString("version"), jo.getString("mods_str"), jo.getInt("mods"), jo.getDouble("od"), jo.getDouble("ar"), jo.getDouble("cs"), jo.getDouble("hp"), jo.getInt("combo"), jo.getInt("max_combo"), jo.getInt("num_circles"), jo.getInt("num_sliders"), jo.getInt("num_spinners"), jo.getInt("misses"), jo.getInt("score_version"), jo.getLong("stars"), jo.getLong("speed_stars"), jo.getLong("aim_stars"), jo.getInt("nsingles"), jo.getInt("nsingles_threshold"), jo.getLong("aim_pp"), jo.getLong("speed_pp"), jo.getLong("acc_pp"), jo.getLong("pp"));
    }

}
