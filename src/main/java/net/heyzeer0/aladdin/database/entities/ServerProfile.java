package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.profiles.GivewayProfile;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;
import net.heyzeer0.aladdin.profiles.custom.ReminderProfile;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;

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

    //Giveways
    //o motivo disso estar aqui é que é muito melhor puxar o dado apenas uma vez doq simplesmente puxar por guilda.
    HashMap<String, GivewayProfile> giveways = new HashMap<>();

    public ServerProfile() {
        this("main", new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    @ConstructorProperties({"id", "users_who_upvoted", "reminders", "giveways"})
    public ServerProfile(String id, ArrayList<String> users_who_upvoted, ArrayList<ReminderProfile> reminders, HashMap<String, GivewayProfile> giveways) {
        this.id = id;
        this.users_who_upvoted = users_who_upvoted;
        this.reminders = reminders;

        if(giveways == null) {
            this.giveways = new HashMap<>();
        }else{
            this.giveways = giveways;
        }
    }

    @JsonIgnore
    public boolean isUserUpvoted(String id) {
        return users_who_upvoted.contains(id);
    }

    @JsonIgnore
    public HashMap<String, GivewayProfile> getGiveways() {
        return giveways;
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

    public void updateGiveways(HashMap<String, GivewayProfile> giveways) {
        this.giveways = giveways;
        saveAsync();
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
