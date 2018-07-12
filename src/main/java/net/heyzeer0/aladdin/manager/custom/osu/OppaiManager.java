package net.heyzeer0.aladdin.manager.custom.osu;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OppaiManager {

    public static OppaiInfo getMapInfo(String map_id, String mods) {
        return Main.getDatabase().getOsuMap(map_id, mods);
    }

    public static OppaiInfo getMapInfoPure(String map_id, String mods) throws Exception {
        Process p = Runtime.getRuntime().exec("./oppai.sh " + map_id + " -ojson " + mods);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        JSONObject jo = new JSONObject(br.readLine());

        p.waitFor();
        p.destroy();

        return new OppaiInfo(jo.getString("oppai_version"), map_id, jo.getInt("code"), jo.getString("errstr"), jo.getString("title"), jo.getString("creator"), jo.getString("version"), jo.getString("mods_str"), jo.getInt("mods"), jo.getDouble("od"), jo.getDouble("ar"), jo.getDouble("cs"), jo.getDouble("hp"), jo.getInt("combo"), jo.getInt("max_combo"), jo.getInt("num_circles"), jo.getInt("num_sliders"), jo.getInt("num_spinners"), jo.getInt("misses"), jo.getInt("score_version"), jo.getLong("stars"), jo.getLong("speed_stars"), jo.getLong("aim_stars"), jo.getInt("nsingles"), jo.getInt("nsingles_threshold"), jo.getLong("aim_pp"), jo.getLong("speed_pp"), jo.getLong("acc_pp"), jo.getLong("pp"));
    }

    public static OppaiInfo getMapInfo(String map_id, OsuMatchProfile op) throws Exception {
        String mods = "";
        for(OsuMods om : op.getMods()) {
            mods = mods + om.getShortName();
        }
        if(mods.equals("")) {
            mods = "none";
        }

        Process p = Runtime.getRuntime().exec("./oppai-full.sh " + map_id + " -ojson " + mods + " " + op.getCount50() + " " + op.getCount100() + " " + op.getCountmiss() + " " + op.getMaxcombo());
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        JSONObject jo = new JSONObject(br.readLine());
        p.waitFor();
        p.destroy();

        return new OppaiInfo(jo.getString("oppai_version"), map_id, jo.getInt("code"), jo.getString("errstr"), jo.getString("title"), jo.getString("creator"), jo.getString("version"), jo.getString("mods_str"), jo.getInt("mods"), jo.getDouble("od"), jo.getDouble("ar"), jo.getDouble("cs"), jo.getDouble("hp"), jo.getInt("combo"), jo.getInt("max_combo"), jo.getInt("num_circles"), jo.getInt("num_sliders"), jo.getInt("num_spinners"), jo.getInt("misses"), jo.getInt("score_version"), jo.getLong("stars"), jo.getLong("speed_stars"), jo.getLong("aim_stars"), jo.getInt("nsingles"), jo.getInt("nsingles_threshold"), jo.getLong("aim_pp"), jo.getLong("speed_pp"), jo.getLong("acc_pp"), jo.getLong("pp"));
    }

}
