package net.heyzeer0.aladdin.configs;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.annotations.Config;
import net.heyzeer0.aladdin.profiles.LogProfile;
import net.heyzeer0.aladdin.utils.FileReader;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 08/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ConfigManager {

    public static LogProfile logger = new LogProfile("Configs");

    public static void lockAndLoad(Class t) throws Exception {
        Config configAnn = (Config) t.getAnnotation(Config.class);

        if(configAnn == null) {
            return;
        }

        File path = new File(Main.getDataFolder(),"configs"); path.mkdirs();
        File base = new File(path, configAnn.name() + ".cfg");
        if(!base.exists()) base.createNewFile();

        HashMap<String, Object> toCreate = new HashMap<>();

        //set na classe definida
        FileReader reader = new FileReader(base);
        for(Field f : t.getDeclaredFields()) {
            if(reader.hasValue(f.getName())) {
                f.set(null, reader.getValue(f.getName()));

                if(reader.getValue(f.getName()).equalsIgnoreCase("<insert-here>")) {
                    logger.alert("Configuration " + configAnn.name() + "." + f.getName() + " has the default value as value!");
                }
            }else{
                toCreate.put(f.getName(), f.get(null));
            }
        }

        //autogen do file
        if(toCreate.size() > 0) {
            FileWriter r = new FileWriter(base);

            for(String k : reader.getValues().keySet()) {
                toCreate.put(k, reader.getValues().get(k));
            }

            for(String x : toCreate.keySet()) {
                r.write("[" + x + "] = " + toCreate.get(x) + "\n");
            }

            r.close();
        }
    }

}
