package net.heyzeer0.aladdin.database;

import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.configs.MainConfig;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
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

    public AladdinData() {
        conn = r.connection().hostname(MainConfig.rethink_ip).port(MainConfig.rethink_port).db(MainConfig.rethink_db).user(MainConfig.rethink_user, MainConfig.rethink_pass).connect();

        try { r.tableCreate("users").runNoReply(conn); r.tableCreate("guilds").runNoReply(conn); }catch (Exception ignored) {}
    }

    public GuildProfile getGuildProfile(Guild u) {
        GuildProfile data = r.table(GuildProfile.DB_TABLE).get(u.getId()).run(conn, GuildProfile.class);
        return data != null ? data : new GuildProfile(u);
    }

    public UserProfile getUserProfile(User u) {
        UserProfile data = r.table(UserProfile.DB_TABLE).get(u.getId()).run(conn, UserProfile.class);
        return data != null ? data : new UserProfile(u);
    }


}
