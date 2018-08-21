package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;

import java.beans.ConstructorProperties;
import java.util.ArrayList;

import static com.rethinkdb.RethinkDB.r;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class UserProfile implements ManagedObject {

    public static final String DB_TABLE = "users";

    String id;

    Integer premiumKeys = 0;
    boolean premiumActive = false;
    long premiumTime = 0;
    boolean autoRenew;
    boolean trialPremium = false;

    String osuUsername;
    ArrayList<String> recommendedBeatmaps = new ArrayList<>();

    public UserProfile(User u) {
        this(u.getId(), 0, false, 0, false, false, "", new ArrayList<>());
    }

    public UserProfile(String id) {
        this(id, 0, false, 0, false, false, "", new ArrayList<>());
    }

    @ConstructorProperties({"id", "premiumKeys", "premiumActive", "premiumTime", "autoRenew", "trialPremium", "osuUsername", "recommendedBeatmaps"})
    public UserProfile(String id, Integer premiumKeys, boolean premiumActive, long premiumTime, boolean autoRenew, boolean trialPremium, String osuUsername, ArrayList<String> recommendedBeatmaps) {
        this.id = id;
        this.premiumKeys = premiumKeys;
        this.premiumActive = premiumActive;
        this.premiumTime = premiumTime;
        this.autoRenew = autoRenew;
        this.trialPremium = trialPremium;
        this.osuUsername = osuUsername;
        this.recommendedBeatmaps = recommendedBeatmaps;
    }

    public void addRecommendedBeatmap(String id) {
        if(recommendedBeatmaps.size() + 1 > 100) {
            recommendedBeatmaps.remove(0);
        }

        recommendedBeatmaps.add(id);
        saveAsync();
    }

    @JsonIgnore
    public boolean isAlreadyRecommendedBeatmap(String id) {
        return recommendedBeatmaps.contains(id);
    }

    public void updateOsuUsername(String nick) {
        this.osuUsername = nick;

        saveAsync();
    }

    public boolean userPremium() {
        if(BotConfig.bot_owner.equals(id)) {
            return true;
        }
        if(!premiumActive) {
            return false;
        }
        if(System.currentTimeMillis() > premiumTime) {

            if(autoRenew && premiumKeys > 0) {
                activatePremium(false);
                return true;
            }

            disablePremium();
            return false;
        }

        return true;
    }

    public void setAutoRenew(boolean value) {
        autoRenew = value;
        saveAsync();
    }

    public boolean activatePremium(boolean from_other) {
        if(premiumKeys <= 0 && !from_other) {
            return false;
        }
        if(premiumActive) {
            return false;
        }
        premiumActive = true;

        if(!from_other) {
            premiumKeys--;
        }

        premiumTime = System.currentTimeMillis() + 2592000000L;

        Guild g = Main.getGuildById(BotConfig.bot_guild_id);
        if(g.isMember(Main.getUserById(id))) {
            g.getController().addRolesToMember(g.getMemberById(id), g.getRoleById(BotConfig.bot_guild_premiumrole_id)).queue();
        }

        saveAsync();
        return true;
    }

    public boolean activateTrialPremium() {
        if(premiumActive) {
           return false;
        }
        if(Main.getDatabase().getServer().isUserUpvoted(getId())) {
            return false;
        }
        premiumActive = true;
        premiumTime = System.currentTimeMillis() + 432000000L;
        trialPremium = true;

        saveAsync();
        return true;
    }

    public boolean disablePremium() {
        if(!premiumActive) {
            return false;
        }

        Guild g = Main.getGuildById(BotConfig.bot_guild_id);
        if(g.isMember(Main.getUserById(id))) {
            g.getController().removeRolesFromMember(g.getMemberById(id), g.getRoleById(BotConfig.bot_guild_premiumrole_id)).queue();
        }

        premiumActive = false;
        premiumTime = 0;
        autoRenew = false;
        trialPremium = false;
        saveAsync();
        return true;
    }

    public void removeKey(Integer amount) {
        premiumKeys-=amount;
        saveAsync();
    }

    public void addKeys(Integer amount) {
        premiumKeys+= amount;
        saveAsync();
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
