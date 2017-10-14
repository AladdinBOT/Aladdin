package net.heyzeer0.aladdin.manager.custom.warframe;

import net.heyzeer0.aladdin.profiles.custom.warframe.AlertProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.MissionProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.RewardID;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HeyZeer0 on 04/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AlertManager {

    public static List<AlertProfile> getAlerts() {
        List<AlertProfile> alerts = new ArrayList<>();
        for (String line : Utils.readBuffer("http://deathsnacks.com/wf/data/alerts_raw.txt")) {
            int count = line.length() - line.replace("|", "").length();
            if (count >= 10) {
                alerts.add(process(line));
            }
        }
        return alerts;
    }

    public static AlertProfile process(String raw) {
        AlertProfile alert = new AlertProfile();
        String[] arr = raw.split("\\|");
        int count = 0;
        alert.setId(getString(arr, count));
        alert.setNode(getString(arr, ++count));
        alert.setRegion(getString(arr, ++count));
        String mission = getString(arr, ++count);
        String faction = getString(arr, ++count);
        alert.setMission(new MissionProfile(mission, faction));
        alert.setMinLevel(getString(arr, ++count));
        alert.setMaxLevel(getString(arr, ++count));
        alert.setActivation(getDate(arr, ++count));
        alert.setExpiry(getDate(arr, ++count));
        String reward = getString(arr, ++count);
        alert.setCredits(getCredits(reward));
        String loot = getLoot(reward);
        alert.setLoot(loot);
        RewardID rewardID = RewardID.getRewardID(loot);
        alert.setRewardID(rewardID);
        alert.setDescription(getString(arr, ++count));
        return alert;
    }

    private static Integer getCredits(String s) {
        int start = s.indexOf(" - ");
        if (start >= 0) {
            s = s.substring(0, start);
        }
        String number = s.replaceAll("\\D", "");
        return Integer.valueOf(number);
    }

    private static String getLoot(String s) {
        int start = s.indexOf(" - ") + 3;
        if (start >= 3 && start < s.length()) {
            s = s.substring(start);
            return s;
        } else {
            return null;
        }
    }

    private static String getString(String[] arr, int index) {
        if (arr.length > index) {
            return arr[index];
        } else {
            return null;
        }
    }

    private static Date getDate(String[] arr, int index) {
        try {
            String d = getString(arr, index);
            return new Date(Integer.valueOf(d) * 1000L);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
