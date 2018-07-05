package net.heyzeer0.aladdin.profiles.custom.osu;

import lombok.Getter;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class OppaiInfo {

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

    public OppaiInfo(String oppai_version, int code, String errstr, String title, String creator, String version, String mods_str, int mods, double od, double ar, double cs, double hp, int combo, int max_combo, int num_circles, int num_sliders, int num_spinners, int misses, int score_version, long stars, long speed_stars, long aim_stars, int nsingles, int nsingles_threshold, long aim_pp, long speed_pp, long acc_pp, long pp) {
        this.oppai_version = oppai_version; this.code = code; this.errstr = errstr; this.title = title;
        this.creator = creator; this.version = version; this.mods_str = mods_str; this.mods = mods; this.od = od; this.ar = ar; this.cs = cs;
        this.hp = hp; this.combo = combo; this.max_combo = max_combo; this.num_circles = num_circles; this.num_spinners = num_spinners; this.num_sliders = num_sliders; this.misses = misses;
        this.score_version = score_version; this.stars = stars; this.speed_stars = speed_stars; this.aim_stars = aim_stars; this.nsingles = nsingles; this.nsingles_threshold = nsingles_threshold;
        this.aim_pp = aim_pp; this.speed_pp = speed_pp; this.acc_pp = acc_pp; this.pp = pp;
    }

}
