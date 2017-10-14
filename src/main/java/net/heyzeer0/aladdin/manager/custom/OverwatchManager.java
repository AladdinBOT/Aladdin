package net.heyzeer0.aladdin.manager.custom;

import net.heyzeer0.aladdin.profiles.custom.OverwatchPlayer;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONObject;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class OverwatchManager {

    private static String api_url = "http://ow-api.herokuapp.com/profile/pc/us/";

    public static OverwatchPlayer getUserProfile(String n) throws Exception {

        String website = Utils.readWebsite(api_url + n);
        if(website.contains("Not Found")) {
            return null;
        }

        JSONObject json = new JSONObject(Utils.readWebsite(api_url + n));

        OverwatchPlayer p = new OverwatchPlayer();

        p.setUsername(json.getString("username"));
        p.setLevel(json.getInt("level"));
        p.setPortrait(json.getString("portrait"));

        JSONObject quickplay = json.getJSONObject("games").getJSONObject("quickplay");
        JSONObject competitive = json.getJSONObject("games").getJSONObject("competitive");

        if(competitive.has("won") && !competitive.isNull("won")) {
            p.setCompetitiveWins(competitive.getInt("won"));
        }
        if(competitive.has("lost") && !competitive.isNull("lost")) {
            p.setCompetitiveLosts(competitive.getInt("lost"));
        }
        if(competitive.has("draw") && !competitive.isNull("draw")) {
            p.setCompetitiveDraw(competitive.getInt("draw"));
        }
        if(competitive.has("played") && !competitive.isNull("played")) {
            p.setCompetitiveMatchs(competitive.getInt("played"));
        }
        if(quickplay.has("won") && !quickplay.isNull("won")) {
            p.setQuickplayWins(quickplay.getInt("won"));
        }

        JSONObject playtime = json.getJSONObject("playtime");
        if(playtime.has("quickplay") && playtime.get("quickplay") != null) {
            p.setQuickplayTime(playtime.getString("quickplay"));
        }
        if(playtime.has("competitive") && playtime.get("competitive") != null) {
            p.setCompetitiveTime(playtime.getString("competitive"));
        }

        JSONObject comp = json.getJSONObject("competitive");
        if(comp.has("rank") && !comp.isNull("rank")) {
            p.setCompetitiveRank(comp.getString("rank"));
        }
        if(comp.has("rank_img") && !comp.isNull("rank_img")) {
            p.setCompetitiveImg(comp.getString("rank_img"));
        }

        return p;
    }

}
