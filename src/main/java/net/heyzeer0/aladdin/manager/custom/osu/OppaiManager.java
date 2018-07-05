package net.heyzeer0.aladdin.manager.custom.osu;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by HeyZeer0 on 05/07/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OppaiManager {

    public static OppaiInfo getMapInfo(int map_id) throws Exception {
        Process p = Runtime.getRuntime().exec("./oppai.sh " + map_id + "\"-ojson\"");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        int count = 0;
        String line;
        while((line = br.readLine()) != null) {
            count++;
            Main.getLogger().warn("[" + count + "] " + line);
        }

        p.waitFor();
        p.destroy();

        return new OppaiInfo(null, 0, null, null, null, null, null, null, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

}
