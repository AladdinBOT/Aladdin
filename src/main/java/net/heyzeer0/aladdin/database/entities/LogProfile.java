package net.heyzeer0.aladdin.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.heyzeer0.aladdin.enums.LogModules;
import net.heyzeer0.aladdin.utils.Utils;

import java.beans.ConstructorProperties;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 27/10/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class LogProfile {

    String channel_id;
    HashMap<LogModules, Boolean> modules = new HashMap<>();

    public LogProfile(String channel_id) {
        this(channel_id, Utils.getLogDefaults());
    }

    @ConstructorProperties({"channel_id", "modules"})
    public LogProfile(String channel_id, HashMap<LogModules, Boolean> modules) {
        this.channel_id = channel_id;
        this.modules = modules;
    }

    @JsonIgnore
    public boolean getLogModuleStatus(LogModules cfg) {
        return modules.getOrDefault(cfg, cfg.isActive());
    }

    public void changeModuleStatus(LogModules cfg, boolean value) {
        modules.replace(cfg, value);
    }

    public void changeChannelID(String id) {
        channel_id = id;
    }


}
