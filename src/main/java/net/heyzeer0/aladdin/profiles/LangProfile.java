package net.heyzeer0.aladdin.profiles;

import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.utils.FileReader;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 09/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LangProfile {


    String url;

    HashMap<String, String> messages = new HashMap<>();

    public LangProfile(String name) {
        this.url = BotConfig.lang_base_url + name + ".lang";

        try{
            loadLang();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public void refreshLang() throws Exception {
        loadLang();
    }

    private void loadLang() throws Exception {
        FileReader reader = new FileReader(url);

        //autogen do file
        for(String k : reader.getValues().keySet()) {
            messages.put(k, reader.getValues().get(k));
        }
    }

    public String getRaw(String key) {
        return messages.get(key);
    }

    public String get(String key) {
        return messages.getOrDefault(key, key).replace("\\n", "\n");
    }

    public String get(String key, Object... obj) {
        return String.format(get(key), obj);
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

}
