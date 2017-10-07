package net.heyzeer0.aladdin.profiles.custom.warframe;

import java.util.Date;
import java.util.Set;

/**
 * Created by HeyZeer0 on 04/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AlertProfile {
    private String id;
    private String node;
    private String region;
    private MissionProfile mission;
    private String minLevel;
    private String maxLevel;
    private Date activation;
    private Date expiry;
    private Integer credits;
    private String loot;
    private RewardID rewardID;
    private String description;

    private boolean ignored;
    private boolean done;
    private boolean matchLoot;
    private boolean matchCredits;
    private boolean matchMission;

    public AlertProfile() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public MissionProfile getMission() {
        return mission;
    }

    public void setMission(MissionProfile mission) {
        this.mission = mission;
    }

    public String getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(String minLevel) {
        this.minLevel = minLevel;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Date getActivation() {
        return activation;
    }

    public void setActivation(Date activation) {
        this.activation = activation;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getLoot() {
        return loot;
    }

    public void setLoot(String loot) {
        this.loot = loot;
    }

    public RewardID getRewordID() {
        return rewardID;
    }

    public void setRewardID(RewardID lootID) {
        this.rewardID = lootID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public void setIgnored(Set<String> filters) {
        if (rewardID == null) {
            ignored = false;
        } else {
            ignored = filters.contains(rewardID.getName());
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isMatchLoot() {
        return matchLoot;
    }

    public void setMatchLoot(boolean matchLoot) {
        this.matchLoot = matchLoot;
    }

    public boolean isMatchCredits() {
        return matchCredits;
    }

    public void setMatchCredits(boolean matchCredits) {
        this.matchCredits = matchCredits;
    }

    public boolean isMatchMission() {
        return matchMission;
    }

    public void setMatchMission(boolean matchMission) {
        this.matchMission = matchMission;
    }

    public String getLocation() {
        return node + " (" + region + ")";
    }

    public boolean isExpired() {
        return expiry.before(new Date());
    }

    public boolean hasLoot() {
        return loot != null;
    }

    public String getTimeLeft() {
        long time = expiry.getTime() - new Date().getTime();
        String timeLeft = "";
        long days = time / (24 * 60 * 60 * 1000);

        long hours = time / (60 * 60 * 1000) % 24;

        long minutes = time / (60 * 1000) % 60;
        if (days != 0) {
            timeLeft = timeLeft + Math.abs(days) + " dia";
            if (Math.abs(days) > 1) {
                timeLeft = timeLeft + "s";
            }
        }
        if (hours != 0) {
            timeLeft = timeLeft + Math.abs(hours) + " hora";
            if (Math.abs(hours) > 1) {
                timeLeft = timeLeft + "s";
            }
        }

        if (minutes != 0 && days == 0) {
            if (hours != 0) {
                timeLeft = timeLeft + " ";
            }
            timeLeft = timeLeft + Math.abs(minutes) + " minuto";
            if (Math.abs(minutes) > 1) {
                timeLeft = timeLeft + "s";
            }
        }
        if (days == 0 && hours == 0 && minutes == 0) {
            return "Menos de um segundo";
        } else {
            if (time > 0) {
                return timeLeft;
            } else {
                return timeLeft + " atrás";
            }
        }
    }


}