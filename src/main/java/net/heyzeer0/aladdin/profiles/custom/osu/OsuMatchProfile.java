package net.heyzeer0.aladdin.profiles.custom.osu;

import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 18/06/2018.
 * Copyright © HeyZeer0 - 2016
 */

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

    ArrayList<OsuMods> mods;

    public OsuMatchProfile(String beatmap_id, String score, String maxcombo, String count300, String count100, String count50, String countmiss, String countkatu, String countgeki, String perfect, String enabled_mods, String user_id, String date, String rank, String pp) {
        this.beatmap_id = beatmap_id; this.score = score; this.maxcombo = maxcombo; this.count300 = count300; this.count100 = count100; this.count50 = count50;
        this.countmiss = countmiss; this.countkatu = countkatu; this.countgeki = countgeki; this.perfect = perfect; this.enabled_mods = enabled_mods; this.user_id = user_id;
        this.date = date; this.rank = rank; this.pp = pp;

        mods = OsuMods.getMods(Integer.valueOf(enabled_mods));
    }

    public OsuMatchProfile(String beatmap_id, String score, String maxcombo, String count300, String count100, String count50, String countmiss, String countkatu, String countgeki, String perfect, String enabled_mods, String user_id, String date, String rank) {
        this.beatmap_id = beatmap_id; this.score = score; this.maxcombo = maxcombo; this.count300 = count300; this.count100 = count100; this.count50 = count50;
        this.countmiss = countmiss; this.countkatu = countkatu; this.countgeki = countgeki; this.perfect = perfect; this.enabled_mods = enabled_mods; this.user_id = user_id;
        this.date = date; this.rank = rank; this.pp = "0";

        mods = OsuMods.getMods(Integer.valueOf(enabled_mods));
    }

    public String toString() {
        return Utils.toMD5(new StringBuilder().append(beatmap_id).append(score).append(maxcombo).append(count300).append(count100).append(count50).append(countmiss).append(countkatu).append(countgeki).append(perfect).append(enabled_mods).append(user_id).append(date).append(rank).append(pp).toString().replace(" ", ""));
    }

    public String getBeatmap_id() {
        return beatmap_id;
    }

    public String getScore() {
        return score;
    }

    public String getMaxcombo() {
        return maxcombo;
    }

    public String getCount300() {
        return count300;
    }

    public String getCount100() {
        return count100;
    }

    public String getCount50() {
        return count50;
    }

    public String getCountmiss() {
        return countmiss;
    }

    public String getCountkatu() {
        return countkatu;
    }

    public String getCountgeki() {
        return countgeki;
    }

    public String getPerfect() {
        return perfect;
    }

    public String getEnabled_mods() {
        return enabled_mods;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public String getRank() {
        return rank;
    }

    public String getPp() {
        return pp;
    }

    public ArrayList<OsuMods> getMods() {
        return mods;
    }
}
