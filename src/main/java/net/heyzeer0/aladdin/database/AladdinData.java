package net.heyzeer0.aladdin.database;

import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.configs.instances.DatabaseConfig;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
import net.heyzeer0.aladdin.database.entities.ServerProfile;
import net.heyzeer0.aladdin.database.entities.UserProfile;
import net.heyzeer0.aladdin.manager.custom.osu.OppaiManager;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;

import java.util.ArrayList;
import java.util.Optional;
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

    private static ArrayList<OppaiInfo> osumaps = new ArrayList<>();

    public AladdinData() {
        conn = r.connection().hostname(DatabaseConfig.rethink_ip).port(Integer.valueOf(DatabaseConfig.rethink_port)).db(DatabaseConfig.rethink_db).user(DatabaseConfig.rethink_user, DatabaseConfig.rethink_pass).connect();

        try { r.tableCreate("users").runNoReply(conn); }catch (Exception ignored) {}
        try { r.tableCreate("guilds").runNoReply(conn); }catch (Exception ignored) {}
        try { r.tableCreate("server").runNoReply(conn); }catch (Exception ignored) {}
        try { r.tableCreate("osumaps").runNoReply(conn); }catch (Exception ignored) {}

        Cursor<OppaiInfo> maps = r.table("osumaps").run(conn, OppaiInfo.class);
        while(maps.hasNext()) {
            osumaps.add(maps.next());
        }

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

    public OppaiInfo getOsuMap(String map_id, String mods) {
        Optional<OppaiInfo> info = osumaps.stream().filter(c -> c.getBeatmap_id().equals(map_id)).filter(c -> c.getMods_str().equals(mods)).findFirst();
        if(info.isPresent()) return info.get();

        try{
            OppaiInfo oinfo = OppaiManager.getMapInfoPure(map_id, mods);
            exec.submit(() -> r.table("osumaps").insert(oinfo).optArg("conflict", "replace").runNoReply(conn));
            return oinfo;
        }catch (Exception ex) { ex.printStackTrace(); }

        return null;
    }

    public boolean isReady() {
        return ready;
    }


}
