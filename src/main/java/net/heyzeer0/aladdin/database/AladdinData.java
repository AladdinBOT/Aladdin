package net.heyzeer0.aladdin.database;

import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.configs.instances.DatabaseConfig;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
import net.heyzeer0.aladdin.database.entities.ServerProfile;
import net.heyzeer0.aladdin.database.entities.UserProfile;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.rethinkdb.RethinkDB.r;
/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AladdinData {

    public Connection conn;
    public static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    boolean ready = false;

    public AladdinData() {
        conn = r.connection().hostname(DatabaseConfig.rethink_ip).port(Integer.valueOf(DatabaseConfig.rethink_port)).db(DatabaseConfig.rethink_db).user(DatabaseConfig.rethink_user, DatabaseConfig.rethink_pass).connect();

        try { r.tableCreate("users").runNoReply(conn); }catch (Exception ignored) {}
        try { r.tableCreate("guilds").runNoReply(conn); }catch (Exception ignored) {}
        try { r.tableCreate("server").runNoReply(conn); }catch (Exception ignored) {}

        ready = true;
    }

    public GuildProfile getGuildProfile(Guild u) {
        GuildProfile data = r.table(GuildProfile.DB_TABLE).get(u.getId()).run(conn, GuildProfile.class);
        return data != null ? data : new GuildProfile(u);
    }

    public UserProfile getUserProfile(User u) {
        UserProfile data = r.table(UserProfile.DB_TABLE).get(u.getId()).run(conn, UserProfile.class);
        return data != null ? data : new UserProfile(u);
    }

    public UserProfile getUserProfile(String id) {
        UserProfile data = r.table(UserProfile.DB_TABLE).get(id).run(conn, UserProfile.class);
        return data != null ? data : new UserProfile(id);
    }

    public ServerProfile getServer() {
        ServerProfile data = r.table(ServerProfile.DB_TABLE).get("main").run(conn, ServerProfile.class);
        return data != null ? data : new ServerProfile();
    }

    public boolean isReady() {
        return ready;
    }


}
