package net.heyzeer0.aladdin.profiles.commands;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ArgumentProfile {

    String[] raw;
    HashMap<String, String> arguments = new HashMap<>();

    public ArgumentProfile(String[] base, String[] parameters) {
        raw = base;
        if(!parameters[0].equalsIgnoreCase("none")) {
            for(int i = 0; i < parameters.length; i++) {
                arguments.put(parameters[i], raw[i]);
            }
        }
    }

    public int getSize() {
        return raw.length;
    }

    public String[] getRaw() {
        return raw;
    }

    public boolean contains(int i) {
        return raw.length < i;
    }

    public String get(int i) {
        return raw[i];
    }

    public String get(String parameter) {
        return arguments.get(parameter);
    }

    public String getComplete() {
        return StringUtils.join(Arrays.copyOfRange(raw, 0, raw.length), " ");
    }

    public String getCompleteAfter(int i) {
        return StringUtils.join(Arrays.copyOfRange(raw, i, raw.length), " ");
    }

}
