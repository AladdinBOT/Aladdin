package net.heyzeer0.aladdin.manager.custom.osu;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OppaiManager {

    public static ArrayList<OppaiQueue> queue = new ArrayList<>();
    private static boolean queueRunning = false;

    public static OppaiInfo getMapInfo(String map_id, String mods) {
        return Main.getDatabase().getOsuMapWD(map_id, mods);
    }

    public static void addMapToQueue(String map_id, String mods) {
        queue.add(new OppaiQueue(map_id, mods));

        processQueue(false);
    }

    private static void processQueue(boolean ignoreRunning) {
        if(queueRunning && !ignoreRunning) return;

        queueRunning = true;
        Utils.runAsync(() -> {
            if(queue.size() >= 1) {
                OppaiQueue q = queue.get(0);
                Main.getDatabase().getOsuMap(q.map_id, q.mods);
                queue.remove(0);

                if(queue.size() >= 1) {
                    processQueue(true);
                }else{
                    queueRunning = false;
                }
            }
        });
    }

    public static OppaiInfo getMapByAcurracy(String map_id, String mods, double acurracy) throws Exception {
        Process p = Runtime.getRuntime().exec("./oppai-acurracy.sh " + map_id + " -ojson " + mods + " " + acurracy);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        JSONObject jo = new JSONObject(br.readLine());

        p.waitFor();
        p.destroy();

        return new OppaiInfo(jo.getString("oppai_version"), map_id, jo.getInt("code"), jo.getString("errstr"), jo.getString("title"), jo.getString("creator"), jo.getString("version"), jo.getString("mods_str"), jo.getInt("mods"), jo.getDouble("od"), jo.getDouble("ar"), jo.getDouble("cs"), jo.getDouble("hp"), jo.getInt("combo"), jo.getInt("max_combo"), jo.getInt("num_circles"), jo.getInt("num_sliders"), jo.getInt("num_spinners"), jo.getInt("misses"), jo.getInt("score_version"), jo.getLong("stars"), jo.getLong("speed_stars"), jo.getLong("aim_stars"), jo.getInt("nsingles"), jo.getInt("nsingles_threshold"), jo.getLong("aim_pp"), jo.getLong("speed_pp"), jo.getLong("acc_pp"), jo.getLong("pp"));
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

    static class OppaiQueue {

        public String map_id;
        public String mods;

        public OppaiQueue(String map_id, String mods) {
            this.map_id = map_id; this.mods = mods;
        }

    }

}
