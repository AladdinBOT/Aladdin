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

    public ArgumentProfile(String[] base) {
        raw = base;

        String total = "";
        for(String x : raw) { total = total + x + " ";}
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

    public String getComplete() {
        return StringUtils.join(Arrays.copyOfRange(raw, 0, raw.length), " ");
    }

    public String getCompleteAfter(int i) {
        return StringUtils.join(Arrays.copyOfRange(raw, i, raw.length), " ");
    }

}
