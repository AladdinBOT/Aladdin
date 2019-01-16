package net.heyzeer0.aladdin.enums;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */

public enum OsuMods {

    NoFail(1, "NF", "mod_no-fail"),
    Easy(2, "EZ", "mod_easy"),
    Hidden(8, "HD", "mod_hidden"),
    HardRock(16, "HR", "mod_hard-rock"),
    SuddenDeath(32, "SD", "mod_sudden-death"),
    DoubleTime(64, "DT", "mod_double-time"),
    Relax(128, null, null),
    HalfTime(256, "HT", null),
    Nightcore(512, "NC", "mod_nightcore"),
    Flashlight(1024, "FL", "mod_flashlight"),
    Autoplay(2048, null, null),
    SpunOut(4096, "SO", null),
    Relax2(8192, null, null),
    Perfect(16384, "PF", "mod_perfect");

    final int id; final String shortName, imageFile; final BufferedImage image;

    OsuMods(int id, String shortName, String imageFile) {
        this.id = id;
        this.shortName = shortName;
        this.imageFile = imageFile;

        if (imageFile != null) {
            BufferedImage image1;
            try {
                image1 = ImageIO.read(new FileInputStream(new File(Main.getDataFolder() + File.separator + "images" + File.separator + "osu", imageFile + ".png")));
            } catch (Exception ex) {
                image1 = null;
            }

            image = image1;
            return;
        }

        image = null;
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
        for(String mm : Utils.splitStringEvery(mods, 2)) {
            for(OsuMods md : values()) {
                if(md.getShortName() == null) continue;

                if(md.getShortName().equalsIgnoreCase(mm)) {
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

    public int getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getImageFile() {
        return imageFile;
    }

    public BufferedImage getImage() {
        return image;
    }

}
