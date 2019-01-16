/*
 * Developed by HeyZeer0 on 11/20/18 1:01 PM.
 * Last Modification 11/20/18 12:56 PM.
 *
 * Copyright HeyZeer0 (c) 2018.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.profiles.GiveawayProfile;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.SubscriptionProfile;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;

import static com.rethinkdb.RethinkDB.r;

public class ServerProfile implements ManagedObject {

    public static final String DB_TABLE = "server";

    ArrayList<String> users_who_upvoted = new ArrayList<>();
    ArrayList<ReminderProfile> reminders = new ArrayList<>();
    String id;
    HashMap<String, GiveawayProfile> giveaways = new HashMap<>();
    HashMap<String, SubscriptionProfile> subscriptions = new HashMap<>();
    ArrayList<String> sendedIds = new ArrayList<>();
    HashMap<String, ArrayList<String>> osu_subscriptions = new HashMap<>();

    public ServerProfile() {
        this("main", new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }

    @ConstructorProperties({"id", "users_who_upvoted", "reminders", "giveaways", "subscriptions", "sendedIds", "osu_subscriptions"})
    public ServerProfile(String id, ArrayList<String> users_who_upvoted, ArrayList<ReminderProfile> reminders, HashMap<String, GiveawayProfile> giveaways, HashMap<String, SubscriptionProfile> subscriptions, ArrayList<String> sendedIds, HashMap<String, ArrayList<String>> osu_subscriptions) {
        this.id = id;
        this.users_who_upvoted = users_who_upvoted;
        this.reminders = reminders;

        boolean save = false;

        if(giveaways == null) {
            this.giveaways = new HashMap<>();
            save = true;
        }else{ this.giveaways = giveaways; }
        if(subscriptions == null) {
            this.subscriptions = new HashMap<>();
            save = true;
        }else { this.subscriptions = subscriptions; }
        if(sendedIds == null) {
            this.sendedIds = new ArrayList<>();
            save = true;
        }else { this.sendedIds = sendedIds; }
        if(osu_subscriptions == null) {
            this.osu_subscriptions = new HashMap<>();
            save = true;
        }else { this.osu_subscriptions = osu_subscriptions; }

        if(save)
            saveAsync();
    }

    @JsonIgnore
    public boolean isUserUpvoted(String id) {
        return users_who_upvoted.contains(id);
    }

    public void addUpvoted(String u) {
        users_who_upvoted.add(u);
        saveAsync();
    }

    public void removeUpvoted(String id) {
        users_who_upvoted.remove(id);
        saveAsync();
    }

    public void addReminder(ReminderProfile rp) {
        reminders.add(rp);
        saveAsync();
    }

    public void removeReminder(ReminderProfile rp) {
        reminders.remove(rp);
        saveAsync();
    }

    public void updateGiveaways(HashMap<String, GiveawayProfile> giveaways) {
        this.giveaways = giveaways;
        saveAsync();
    }

    public void updateSubscriptions(HashMap<String, SubscriptionProfile> subscriptions) {
        this.subscriptions = subscriptions;
        saveAsync();
    }

    public void updateSendedIds(ArrayList<String> sendedIds) {
        this.sendedIds = sendedIds;
        saveAsync();
    }

    public void updateOsuSubscriptions(HashMap<String, ArrayList<String>> osu_subscriptions) {
        this.osu_subscriptions = osu_subscriptions;
        saveAsync();
    }

    public ArrayList<String> getUsers_who_upvoted() {
        return users_who_upvoted;
    }

    public ArrayList<ReminderProfile> getReminders() {
        return reminders;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, GiveawayProfile> getGiveaways() {
        return giveaways;
    }

    public HashMap<String, SubscriptionProfile> getSubscriptions() {
        return subscriptions;
    }

    public ArrayList<String> getSendedIds() {
        return sendedIds;
    }

    public HashMap<String, ArrayList<String>> getOsu_subscriptions() {
        return osu_subscriptions;
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
