package net.heyzeer0.aladdin.enums;

import com.google.common.base.Splitter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public enum OsuMods {

    NoFail(1, "NF"),
    Easy(2, "EZ"),
    Hidden(8, "HD"),
    HardRock(16, "HR"),
    SuddenDeath(32, "SD"),
    DoubleTime(64, "DT"),
    Relax(128, null),
    HalfTime(256, "HT"),
    Nightcore(512, "NC"),
    Flashlight(1024, "FL"),
    Autoplay(2048, null),
    SpunOut(4096, "SO"),
    Relax2(8192, null),
    Perfect(16384, "PF");

    int id; String shortName;

    OsuMods(int id, String shortName) {
        this.id = id; this.shortName = shortName;
    }

    public boolean valid(int mods) {
        return (mods & id) == id;
    }

    public static ArrayList<OsuMods> getMods(int mods) {
        ArrayList<OsuMods> rm = new ArrayList<>();

        for(OsuMods md : values()) {
            if(md.valid(mods)) rm.add(md);
        }

        return rm;
    }

    public static int fromString(String mods) {
        if(mods.equals("") || mods.length() < 2) return 0;

        int result = 0;
        Iterator<String> it = Splitter.fixedLength(2).split(mods.toUpperCase()).iterator();
        while(it.hasNext()) {
            for(OsuMods md : values()) {
                if(md.getShortName() == null) continue;

                if(md.getShortName().equalsIgnoreCase(it.next())) {
                    result+=md.id;
                    break;
                }
            }
        }

        return result;
    }

    public static String asString(ArrayList<OsuMods> mods) {
        if(mods.size() <= 0) return "";
        String result = "";
        for (OsuMods mod : mods) {
            result = result + mod.getShortName();
        }
        return result;
    }
}
