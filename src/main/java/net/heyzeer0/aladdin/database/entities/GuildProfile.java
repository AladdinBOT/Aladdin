package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.MainConfig;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.profiles.commands.CustomCommand;
import net.heyzeer0.aladdin.utils.Utils;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;

import static com.rethinkdb.RethinkDB.r;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class GuildProfile implements ManagedObject {

    public static final String DB_TABLE = "guilds";

    //Guild data
    String id;
    String ownerId;

    //Configs
    HashMap<GuildConfig, Object> configs = Utils.getDefaultValues();

    //Groups
    HashMap<String, GroupProfile> groups = new HashMap<>();
    HashMap<String, String> user_group = new HashMap<>();
    HashMap<String, ArrayList<String>> user_overrides = new HashMap<>();

    //Commands
    HashMap<String, CustomCommand> commands = new HashMap<>();

    //Starboards
    HashMap<String, StarboardProfile> starboards = new HashMap<>();

    public GuildProfile(Guild u) {
        this(u.getId(), u.getOwner().getUser().getId(), Utils.getDefaultValues(), Utils.getDefaultGroup(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @ConstructorProperties({"id", "ownerId", "configs", "groups", "user_group", "user_overrides", "commands", "starboards"})
    public GuildProfile(String id, String ownerId, HashMap<GuildConfig, Object> configs, HashMap<String, GroupProfile> groups, HashMap<String, String> user_group, HashMap<String, ArrayList<String>> user_overrides, HashMap<String, CustomCommand> commands, HashMap<String, StarboardProfile> starboards) {
        this.id = id;
        this.ownerId = ownerId;
        this.configs = configs;
        this.groups = groups;
        this.user_group = user_group;
        this.user_overrides = user_overrides;
        this.commands = commands;
        this.starboards = starboards;
    }

    @JsonIgnore
    public Object getConfigValue(GuildConfig cfg) {
        return configs.getOrDefault(cfg, cfg.getDefault());
    }

    @JsonIgnore
    public boolean hasCustomCommand(String name) {
        return commands.containsKey(name);
    }

    @JsonIgnore
    public CustomCommand getCustomCommand(String name) {
        return commands.getOrDefault(name, null);
    }

    @JsonIgnore
    public boolean hasPermission(Member u, String permission) {
        if(u.getUser().getId().equals(MainConfig.bot_owner)) {
            return true;
        }
        if(u.getUser().getId().equals(ownerId)) {
            return true;
        }
        if(user_overrides.containsKey(u.getUser().getId())) {
            if(user_overrides.get(u.getUser().getId()).contains("-" + permission)) {
                return false;
            }
            if(user_overrides.get(u.getUser().getId()).contains(permission)) {
                return true;
            }
        }

        if(user_group.containsKey(u.getUser().getId())) {

            if(permission.contains(".")) {
                for(String x : permission.split("\\.")) {
                    if (groups.get(user_group.get(u.getUser().getId())).permissions.contains(x + ".*")) {
                        return true;
                    }
                }
            }

            return groups.get(user_group.get(u.getUser().getId())).permissions.contains(permission);
        }

        if(u.getRoles().size() >= 1) {

            boolean found = false;

            for(Role r : u.getRoles()) {
                if(groups.containsKey(r.getName().toLowerCase())) {

                    if(permission.contains(".")) {
                        for(String x : permission.split("\\.")) {
                            if (groups.get(r.getName().toLowerCase()).permissions.contains(x + ".*")) {
                                found = true;
                                break;
                            }
                        }
                        if(found) break;
                    }

                    if(groups.get(r.getName().toLowerCase()).permissions.contains(permission)) {
                        found = true;
                        break;
                    }
                }
            }

            if(found) {
                return true;
            }

        }

        return getGroupByName(getDefaultGroup()).permissions.contains(permission);
    }

    @JsonIgnore
    public String getDefaultGroup() {
        for(String x : groups.keySet()) {
            if(groups.get(x).isDefault()) {
                return x;
            }
        }

        return null;
    }

    @JsonIgnore
    public GroupProfile getGroupByName(String x) {
        return groups.getOrDefault(x, null);
    }

    public boolean addUserOverride(User u, String x) {
        if(!user_overrides.containsKey(u.getId())) {
           user_overrides.put(u.getId(), new ArrayList<>());
        }
        if(user_overrides.get(u.getId()).contains(x)) {
            return false;
        }
        user_overrides.get(u.getId()).add(x);
        saveAsync();
        return true;
    }

    public boolean removeUserOverride(User u, String x) {
        if(!user_overrides.containsKey(u.getId())) {
            user_overrides.put(u.getId(), new ArrayList<>());
        }
        if(!user_overrides.get(u.getId()).contains(x)) {
            return false;
        }
        user_overrides.get(u.getId()).remove(x);
        saveAsync();
        return true;
    }

    public boolean updateUserGroup(User u, String x) {
        if(!groups.containsKey(x)) {
            return false;
        }
        user_group.put(u.getId(), x);
        saveAsync();
        return true;
    }

    public boolean removeUserGroup(User u) {
        if(!user_group.containsKey(u.getId())) {
            return false;
        }
        user_group.remove(u.getId());
        saveAsync();
        return true;
    }

    public boolean createGroup(GroupProfile pf) {
        if(groups.containsKey(pf.getId())) {
            return false;
        }

        if(pf.isDefault()) {
            for(String x : groups.keySet()) {
                if(groups.get(x).isDefault()) {
                    groups.get(x).setDefault(false);
                    break;
                }
            }
        }

        groups.put(pf.getId(), pf);
        saveAsync();
        return true;
    }

    public boolean deleteGroup(String name) {
        if(!groups.containsKey(name)) {
            return false;
        }

        if(getGroupByName(name).isDefault()) {
            return false;
        }

        groups.remove(name);
        saveAsync();
        return true;
    }

    public boolean addGroupPermission(String group, String perm) {
        if(!groups.containsKey(group)) {
            return false;
        }
        groups.get(group).addPermission(perm);
        saveAsync();
        return true;
    }


    public boolean removeGroupPermission(String group, String perm) {
        if(!groups.containsKey(group)) {
            return false;
        }
        groups.get(group).removePermission(perm);
        saveAsync();
        return true;
    }

    public boolean addUserPermission(User u, String perm) {
        if(!user_overrides.containsKey(u.getId())) {
            return false;
        }
        user_overrides.get(u.getId()).add(perm);
        saveAsync();
        return true;
    }

    public boolean changeConfig(GuildConfig cfg, Object value) {
        if(cfg.getDefault().getClass() == value.getClass()) {
            configs.put(cfg, value);
            saveAsync();
            return true;
        }
        return false;
    }

    public boolean createCustomCommand(String nome, String msg, String author) {
        if(commands.containsKey(nome)) {
            return false;
        }
        commands.put(nome, new CustomCommand(msg, author));
        saveAsync();
        return true;
    }

    public boolean deleteCustomCommand(String nome) {
        if(!commands.containsKey(nome)) {
            return false;
        }
        commands.remove(nome);
        saveAsync();
        return true;
    }

    public void cleanUserData(User u) {
        user_overrides.remove(u.getId());
        user_group.remove(u.getId());
        saveAsync();
    }

    public boolean createStarboard(String emoji, int amount, long channel_id) {
        if(starboards.containsKey(emoji)) {
            return false;
        }
        starboards.put(emoji, new StarboardProfile(emoji, channel_id, amount));
        saveAsync();
        return true;
    }

    public boolean deleteStarboard(int id) {
        if(starboards.size() < id) {
            return false;
        }
        starboards.remove(id);
        saveAsync();
        return true;
    }

    public void checkStarboardAdd(MessageReactionAddEvent e) {
        if(starboards.containsKey(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId())) {
            if(starboards.get(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId()).addToStarboard(e)) {
                saveAsync();
            }
        }
    }

    public void checkStarboardRemove(MessageReactionRemoveEvent e) {
        if(starboards.containsKey(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId())) {
            if(starboards.get(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId()).removeFromStarboard(e)) {
                saveAsync();
            }
        }
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}