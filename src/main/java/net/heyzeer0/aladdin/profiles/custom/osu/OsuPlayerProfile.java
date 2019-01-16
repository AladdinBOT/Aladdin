package net.heyzeer0.aladdin.profiles.custom.osu;


/**
 * Created by HeyZeer0 on 19/12/2016.
 * Copyright © HeyZeer0 - 2016
 */

public class OsuPlayerProfile {

    public String nome;
    public String userid;
    public String count300;
    public String count100;
    public String count50;
    public String playcount;
    public String ranked_score;
    public String total_score;
    public String pp_rank;
    public String level;
    public String accuracy;
    public String count_rank_ss;
    public String count_rank_s;
    public String count_rank_a;
    public String count_rank_ssh;
    public String count_rank_sh;
    public String country;
    public String pp_raw;
    public String country_rank;
    public boolean exist;

    public OsuPlayerProfile(String user) {
        nome = user;
    }

    public String getNome() {
        return nome;
    }

    public String getUserid() {
        return userid;
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

    public String getPlaycount() {
        return playcount;
    }

    public String getRanked_score() {
        return ranked_score;
    }

    public String getTotal_score() {
        return total_score;
    }

    public String getPp_rank() {
        return pp_rank;
    }

    public String getLevel() {
        return level;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public String getCount_rank_ss() {
        return count_rank_ss;
    }

    public String getCount_rank_s() {
        return count_rank_s;
    }

    public String getCount_rank_a() {
        return count_rank_a;
    }

    public String getCount_rank_ssh() {
        return count_rank_ssh;
    }

    public String getCount_rank_sh() {
        return count_rank_sh;
    }

    public String getCountry() {
        return country;
    }

    public String getPp_raw() {
        return pp_raw;
    }

    public String getCountry_rank() {
        return country_rank;
    }

    public boolean isExist() {
        return exist;
    }
}
