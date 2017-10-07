package net.heyzeer0.aladdin.profiles.custom.warframe;

/**
 * Created by HeyZeer0 on 04/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class RewardID {
    private final String name;
    private final String searchURL;
    private final String directURL;

    public RewardID(String name) {
        this.name = name;
        this.searchURL = "http://warframe.wikia.com/wiki/Special:Search?fulltext=Search&search=" + name.replace(" ", "+");
        this.directURL = "http://warframe.wikia.com/wiki/" + name.replace(" ", "_");
    }

    public String getName() {
        return name;
    }

    public String getSearchURL() {
        return searchURL;
    }

    public String getDirectURL() {
        return directURL;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RewardID other = (RewardID) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public static RewardID getRewardID(String loot) {
        if (loot == null) {
            return null;
        }
        String reward = loot.replace("Blueprint", "")
                .replace("Skin", "")
                .replace(",", "")
                .replaceAll("\\d", "")
                .trim();
        if (reward.equals("cr")) {
            return null;
        } else {
            return new RewardID(reward);
        }
    }
}