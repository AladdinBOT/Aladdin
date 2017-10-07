package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
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

    ArrayList<String> playlist;

    public UserProfile(User u) {
        this(u.getId(), 0, false, 0, false, new ArrayList<>());
    }

    @ConstructorProperties({"id", "premiumKeys", "premiumActive", "premiumTime", "autoRenew", "playlist"})
    public UserProfile(String id, Integer premiumKeys, boolean premiumActive, long premiumTime, boolean autoRenew, ArrayList<String> playlist) {
        this.id = id;
        this.premiumKeys = premiumKeys;
        this.premiumActive = premiumActive;
        this.premiumTime = premiumTime;
        this.autoRenew = autoRenew;
        this.playlist = playlist;
    }

    public boolean userPremium() {
        if(!premiumActive) {
            return false;
        }
        if(System.currentTimeMillis() > premiumTime) {

            if(autoRenew && premiumKeys > 0) {
                activatePremium(false);
                return true;
            }

            premiumActive = false;
            premiumTime = 0;
            autoRenew = false;
            saveAsync();
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
