package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;

import java.beans.ConstructorProperties;
import java.util.ArrayList;

import static com.rethinkdb.RethinkDB.r;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class ServerProfile implements ManagedObject {

    public static final String DB_TABLE = "server";

    ArrayList<String> users_who_upvoted = new ArrayList<>();
    ArrayList<ReminderProfile> reminders = new ArrayList<>();
    String id;

    public ServerProfile() {
        this("main", new ArrayList<>(), new ArrayList<>());
    }

    @ConstructorProperties({"id", "users_who_upvoted", "reminders"})
    public ServerProfile(String id, ArrayList<String> users_who_upvoted, ArrayList<ReminderProfile> reminders) {
        this.id = id;
        this.users_who_upvoted = users_who_upvoted;
        this.reminders = reminders;
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

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
