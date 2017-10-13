package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.security.ntlm.Server;
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
public class ServerProfile implements ManagedObject {

    public static final String DB_TABLE = "server";

    ArrayList<String> users_who_upvoted = new ArrayList<>();
    String id;

    public ServerProfile() {
        this("main", new ArrayList<>());
    }

    @ConstructorProperties({"id", "users_who_upvoted"})
    public ServerProfile(String id, ArrayList<String> users_who_upvoted) {
        this.id = id;
        this.users_who_upvoted = users_who_upvoted;
    }

    @JsonIgnore
    public boolean isUserUpvoted(String id) {
        return users_who_upvoted.contains(id);
    }

    public void addUpvoted(User u) {
        users_who_upvoted.add(u.getId());
        saveAsync();
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}
