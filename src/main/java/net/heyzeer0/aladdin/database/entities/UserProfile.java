package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;

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

    HashMap<String, ArrayList<PlaylistTrackProfile>> playlist;

    public UserProfile(User u) {
        this(u.getId(), 0, false, 0, false, new HashMap<>(), false);
    }

    public UserProfile(String id) {
        this(id, 0, false, 0, false, new HashMap<>(), false);
    }

    @ConstructorProperties({"id", "premiumKeys", "premiumActive", "premiumTime", "autoRenew", "playlist", "trialPremium"})
    public UserProfile(String id, Integer premiumKeys, boolean premiumActive, long premiumTime, boolean autoRenew, HashMap<String, ArrayList<PlaylistTrackProfile>> playlist, boolean trialPremium) {
        this.id = id;
        this.premiumKeys = premiumKeys;
        this.premiumActive = premiumActive;
        this.premiumTime = premiumTime;
        this.autoRenew = autoRenew;
        this.playlist = playlist;
        this.trialPremium = trialPremium;
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
            trialPremium = false;
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

    public boolean createPlaylist(String name) {
        if(playlist.containsKey(name)) {
            return false;
        }

        playlist.put(name, new ArrayList<>());
        saveAsync();
        return true;
    }

    public boolean deletePlaylist(String name) {
        if(!playlist.containsKey(name)) {
            return false;
        }

        playlist.remove(name);
        saveAsync();
        return true;
    }

    public boolean addTrackToPlaylist(String playlistn, String name, String duration, String url) {
        if(!playlist.containsKey(playlistn)) {
            return false;
        }
        ArrayList<PlaylistTrackProfile> tracks = playlist.get(playlistn);
        tracks.add(new PlaylistTrackProfile(name, duration, url));

        playlist.put(playlistn, tracks);
        saveAsync();
        return true;
    }

    public boolean removeTrackFromPlaylist(String playlistn, int id) {
        if(!playlist.containsKey(playlistn)) {
            return false;
        }

        if(playlist.get(playlistn).size() < (id)) {
            return false;
        }

        playlist.get(playlistn).remove((id));
        saveAsync();
        return true;
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
