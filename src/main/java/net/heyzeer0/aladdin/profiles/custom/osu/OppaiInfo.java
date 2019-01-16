package net.heyzeer0.aladdin.profiles.custom.osu;

import net.heyzeer0.aladdin.utils.Utils;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */

public class OppaiInfo {

    String id;

    String beatmap_id;
    String oppai_version;
    int code;
    String errstr;
    String title;
    String creator;
    String version;
    String mods_str;
    int mods;
    double od;
    double ar;
    double cs;
    double hp;
    int combo;
    int max_combo;
    int num_circles;
    int num_sliders;
    int num_spinners;
    int misses;
    int score_version;
    long stars;
    long speed_stars;
    long aim_stars;
    int nsingles;
    int nsingles_threshold;
    long aim_pp;
    long speed_pp;
    long acc_pp;
    long pp;

    @ConstructorProperties({"id", "beatmap_id", "oppai_version", "code", "errstr", "title", "creator", "version", "mods_str", "mods", "od", "ar", "cs", "hp", "combo", "max_combo", "num_circles", "num_sliders", "num_spinners",
            "misses", "score_version", "stars", "speed_stars", "aim_stars", "nsingles", "nsingles_threshold", "aim_pp", "speed_pp", "acc_pp", "pp"})
    public OppaiInfo(String id, String beatmap_id, String oppai_version, int code, String errstr, String title, String creator, String version, String mods_str, int mods, double od, double ar, double cs, double hp, int combo, int max_combo, int num_circles, int num_sliders, int num_spinners, int misses, int score_version, long stars, long speed_stars, long aim_stars, int nsingles, int nsingles_threshold, long aim_pp, long speed_pp, long acc_pp, long pp) {
        this.id = id; this.beatmap_id = beatmap_id;
        this.oppai_version = oppai_version; this.code = code; this.errstr = errstr; this.title = title;
        this.creator = creator; this.version = version; this.mods_str = mods_str; this.mods = mods; this.od = od; this.ar = ar; this.cs = cs;
        this.hp = hp; this.combo = combo; this.max_combo = max_combo; this.num_circles = num_circles; this.num_spinners = num_spinners; this.num_sliders = num_sliders; this.misses = misses;
        this.score_version = score_version; this.stars = stars; this.speed_stars = speed_stars; this.aim_stars = aim_stars; this.nsingles = nsingles; this.nsingles_threshold = nsingles_threshold;
        this.aim_pp = aim_pp; this.speed_pp = speed_pp; this.acc_pp = acc_pp; this.pp = pp;
    }

    public OppaiInfo(String oppai_version, String beatmap_id, int code, String errstr, String title, String creator, String version, String mods_str, int mods, double od, double ar, double cs, double hp, int combo, int max_combo, int num_circles, int num_sliders, int num_spinners, int misses, int score_version, long stars, long speed_stars, long aim_stars, int nsingles, int nsingles_threshold, long aim_pp, long speed_pp, long acc_pp, long pp) {
        this.id = Utils.toMD5(beatmap_id + mods_str); this.beatmap_id = beatmap_id;
        this.oppai_version = oppai_version; this.code = code; this.errstr = errstr; this.title = title;
        this.creator = creator; this.version = version; this.mods_str = mods_str; this.mods = mods; this.od = od; this.ar = ar; this.cs = cs;
        this.hp = hp; this.combo = combo; this.max_combo = max_combo; this.num_circles = num_circles; this.num_spinners = num_spinners; this.num_sliders = num_sliders; this.misses = misses;
        this.score_version = score_version; this.stars = stars; this.speed_stars = speed_stars; this.aim_stars = aim_stars; this.nsingles = nsingles; this.nsingles_threshold = nsingles_threshold;
        this.aim_pp = aim_pp; this.speed_pp = speed_pp; this.acc_pp = acc_pp; this.pp = pp;
    }

    public String getId() {
        return id;
    }

    public String getBeatmap_id() {
        return beatmap_id;
    }

    public String getOppai_version() {
        return oppai_version;
    }

    public int getCode() {
        return code;
    }

    public String getErrstr() {
        return errstr;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getVersion() {
        return version;
    }

    public String getMods_str() {
        return mods_str;
    }

    public int getMods() {
        return mods;
    }

    public double getOd() {
        return od;
    }

    public double getAr() {
        return ar;
    }

    public double getCs() {
        return cs;
    }

    public double getHp() {
        return hp;
    }

    public int getCombo() {
        return combo;
    }

    public int getMax_combo() {
        return max_combo;
    }

    public int getNum_circles() {
        return num_circles;
    }

    public int getNum_sliders() {
        return num_sliders;
    }

    public int getNum_spinners() {
        return num_spinners;
    }

    public int getMisses() {
        return misses;
    }

    public int getScore_version() {
        return score_version;
    }

    public long getStars() {
        return stars;
    }

    public long getSpeed_stars() {
        return speed_stars;
    }

    public long getAim_stars() {
        return aim_stars;
    }

    public int getNsingles() {
        return nsingles;
    }

    public int getNsingles_threshold() {
        return nsingles_threshold;
    }

    public long getAim_pp() {
        return aim_pp;
    }

    public long getSpeed_pp() {
        return speed_pp;
    }

    public long getAcc_pp() {
        return acc_pp;
    }

    public long getPp() {
        return pp;
    }
}
