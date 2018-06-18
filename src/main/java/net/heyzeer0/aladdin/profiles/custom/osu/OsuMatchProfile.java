package net.heyzeer0.aladdin.profiles.custom.osu;

import lombok.Getter;

/**
 * Created by HeyZeer0 on 18/06/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class OsuMatchProfile {

    String beatmap_id;
    String score;
    String maxcombo;
    String count300;
    String count100;
    String count50;
    String countmiss;
    String countkatu;
    String countgeki;
    String perfect;
    String enabled_mods;
    String user_id;
    String date;
    String rank;
    String pp;

    public OsuMatchProfile(String beatmap_id, String score, String maxcombo, String count300, String count100, String count50, String countmiss, String countkatu, String countgeki, String perfect, String enabled_mods, String user_id, String date, String rank, String pp) {
        this.beatmap_id = beatmap_id; this.score = score; this.maxcombo = maxcombo; this.count300 = count300; this.count100 = count100; this.count50 = count50;
        this.countmiss = countmiss; this.countkatu = countkatu; this.countgeki = countgeki; this.perfect = perfect; this.enabled_mods = enabled_mods; this.user_id = user_id;
        this.date = date; this.rank = rank; this.pp = pp;
    }


}
