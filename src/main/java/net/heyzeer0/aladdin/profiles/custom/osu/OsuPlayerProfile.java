package net.heyzeer0.aladdin.profiles.custom.osu;

import lombok.Getter;

/**
 * Created by HeyZeer0 on 19/12/2016.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
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

}
