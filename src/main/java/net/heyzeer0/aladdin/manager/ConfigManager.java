package net.heyzeer0.aladdin.manager;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.yaml.file.YamlConfiguration;
import net.heyzeer0.aladdin.interfaces.annotation.YamlConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ConfigManager {

    public static void lockAndLoad(Class<?> x) throws IllegalAccessException, IOException {
        YamlConfig ann = x.getAnnotation(YamlConfig.class);
        if(ann != null) {
            if(!Main.getDataFolder().exists()) {
                Main.getDataFolder().mkdir();
            }

            File config_file;


            if(ann.folder().equalsIgnoreCase("none")) {
                config_file = new File(Main.getDataFolder(), ann.name() + ".yml");
            }else{

                if(!new File(Main.getDataFolder() + File.separator + ann.folder()).exists()) {
                    new File(Main.getDataFolder() + File.separator + ann.folder()).mkdir();
                }

                config_file = new File(Main.getDataFolder() + File.separator + ann.folder(), ann.name() + ".yml");
            }

            boolean newconfig = false;

            if(!config_file.exists()) {
                newconfig = true;
                config_file.createNewFile();
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(config_file);

            if(newconfig) {
                for(Field fd : x.getFields()) {
                    Object obj = fd.get(null);
                    if(obj.getClass() == String.class) {
                        config.set(fd.getName(), obj);
                    }
                }
                config.save(config_file);
                return;
            }

            boolean save = false;

            for(Field fd : x.getFields()) {
                if(!config.contains(fd.getName())) {
                    config.set(fd.getName(), fd.get(null));
                    save = true;
                }
                if(fd.get(null).getClass() == String.class) {
                    fd.set(null, config.getString(fd.getName()));
                }
                if(fd.get(null).getClass() == int.class) {
                    fd.set(null, config.getInt(fd.getName()));
                }
                if(fd.get(null).getClass() == Integer.class) {
                    fd.set(null, config.getInt(fd.getName()));
                }
            }
            if(save) {
                config.save(config_file);
            }
        }
    }

}
