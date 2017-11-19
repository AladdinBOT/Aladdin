package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.MainConfig;
import net.heyzeer0.aladdin.database.entities.profiles.GroupProfile;
import net.heyzeer0.aladdin.database.entities.profiles.LogProfile;
import net.heyzeer0.aladdin.database.entities.profiles.StarboardProfile;
import net.heyzeer0.aladdin.database.interfaces.ManagedObject;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.enums.LogModules;
import net.heyzeer0.aladdin.profiles.commands.CustomCommand;
import net.heyzeer0.aladdin.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.ConstructorProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InvalidObjectException;
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

    //Log
    LogProfile guild_log;

    public GuildProfile(Guild u) {
        this(u.getId(), u.getOwner().getUser().getId(), Utils.getDefaultValues(), Utils.getDefaultGroup(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new LogProfile(null));
    }

    @ConstructorProperties({"id", "ownerId", "configs", "groups", "user_group", "user_overrides", "commands", "starboards", "guild_log"})
    public GuildProfile(String id, String ownerId, HashMap<GuildConfig, Object> configs, HashMap<String, GroupProfile> groups, HashMap<String, String> user_group, HashMap<String, ArrayList<String>> user_overrides, HashMap<String, CustomCommand> commands, HashMap<String, StarboardProfile> starboards, LogProfile guild_log) {
        this.id = id;
        this.ownerId = ownerId;
        this.configs = configs;
        this.groups = groups;
        this.user_group = user_group;
        this.user_overrides = user_overrides;
        this.commands = commands;
        this.starboards = starboards;
        this.guild_log = guild_log;

        if(this.starboards == null) { this.starboards = new HashMap<>(); }
        if(this.guild_log == null) { this.guild_log = new LogProfile(null); }
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

        return getGroupByName(getDefaultGroupName()).permissions.contains(permission);
    }

    @JsonIgnore
    public String getDefaultGroupName() {
        for(String x : groups.keySet()) {
            if(groups.get(x).isDefault()) {
                return x;
            }
        }

        return null;
    }

    @JsonIgnore
    public GroupProfile getDefaultGroup() {
        for(String x : groups.keySet()) {
            if(groups.get(x).isDefault()) {
                return groups.get(x);
            }
        }

        return null;
    }

    @JsonIgnore
    public GroupProfile getGroupByName(String x) {
        return groups.getOrDefault(x, null);
    }

    @JsonIgnore
    public boolean isLogModuleActive(LogModules module) {
        return guild_log.getLogModuleStatus(module);
    }

    public void sendLogMessage(Guild g, EmbedBuilder b) {
        if(guild_log.getChannel_id() != null) {
            TextChannel ch = g.getTextChannelById(guild_log.getChannel_id());
            if(ch == null) {
                guild_log.changeChannelID(null);
                saveAsync();
                return;
            }

            ch.sendMessage(b.build()).queue();
        }
    }

    public void sendLogMessage(Guild g, BufferedImage img, EmbedBuilder b) {
        if(guild_log.getChannel_id() != null) {
            TextChannel ch = g.getTextChannelById(guild_log.getChannel_id());
            if(ch == null) {
                guild_log.changeChannelID(null);
                saveAsync();
                return;
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(img, "png", os);
            } catch (Exception ex) { ex.printStackTrace();}
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            b.setImage("attachment://embed.png");

            ch.sendFile(is, "embed.png", new MessageBuilder().setEmbed(b.build()).build()).queue();
        }
    }

    public void changeLogModuleStatus(LogModules cfg, boolean value) {
        guild_log.changeModuleStatus(cfg, value);
        saveAsync();
    }

    public void changeLogChannel(String channel_id) {
        guild_log.changeChannelID(channel_id);
        saveAsync();
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

    public boolean createStarboard(String emoji, int amount, String channel_id) {
        if(starboards.containsKey(emoji)) {
            return false;
        }
        starboards.put(emoji, new StarboardProfile(emoji, amount, channel_id));
        saveAsync();
        return true;
    }

    public boolean deleteStarboard(int id) {
        if(starboards.size() < id) {
            return false;
        }
        starboards.remove(starboards.keySet().toArray(new String[] {})[id]);
        saveAsync();
        return true;
    }

    public void checkStarboardAdd(MessageReactionAddEvent e) {
        if(starboards == null) {
            starboards = new HashMap<>();
            saveAsync();
            return;
        }
        if(starboards.containsKey(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId())) {
            try{
                if(starboards.get(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId()).addReaction(e)) {
                    saveAsync();
                }
            }catch (InvalidObjectException ex) {
                starboards.remove(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId());
                saveAsync();
            }
        }
    }

    public void checkStarboardRemove(MessageReactionRemoveEvent e) {
        if(starboards == null) {
            starboards = new HashMap<>();
        }
        if(starboards.containsKey(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId())) {
            try{
                if(starboards.get(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId()).removeReaction(e)) {
                    saveAsync();
                }
            }catch (InvalidObjectException ex) {
                starboards.remove(e.getReactionEmote().getName() + "|" + e.getReactionEmote().getId());
                saveAsync();
            }
        }
    }

    @Override
    public void delete() { r.table(DB_TABLE).get(getId()).delete().runNoReply(Main.getDatabase().conn); }

    @Override
    public void save() { r.table(DB_TABLE).insert(this).optArg("conflict", "replace").runNoReply(Main.getDatabase().conn); }

}