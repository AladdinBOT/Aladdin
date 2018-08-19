package net.heyzeer0.aladdin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 08/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class FileReader {

    File f;
    String url;

    HashMap<String, String> values = new HashMap<>();

    public FileReader(File f) throws Exception {
        this.f = f;

        parseFile();
    }

    public FileReader(String url) throws Exception {
        this.url = url;

        parseUrl();
    }

    private void parseUrl() throws Exception {
        String response = new Router(url).getResponse().getResult();

        String[] ss = response.split("\n");
        for(String ctnt : ss) {
            if(!ctnt.contains("[") || !ctnt.contains("]") || !ctnt.contains("=")) continue;
            String[] splited;

            if(ctnt.contains(" = ")) {
                splited = ctnt.split(" = ");
            }else { splited = ctnt.split("="); }

            values.put(splited[0].replace("[", "").replace("]", ""), splited[1]);
        }
    }

    private void parseFile() throws Exception {
        java.io.FileReader r = new java.io.FileReader(f);
        BufferedReader reader = new BufferedReader(r);

        String ctnt;
        while((ctnt = reader.readLine()) != null) {
            if(!ctnt.contains("[") || !ctnt.contains("]") || !ctnt.contains("=")) continue;
            String[] splited;

            if(ctnt.contains(" = ")) {
                splited = ctnt.split(" = ");
            }else { splited = ctnt.split("="); }

            values.put(splited[0].replace("[", "").replace("]", ""), splited[1]);
        }

        r.close();
        reader.close();
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

    public boolean hasValue(String key) {
        return values.containsKey(key);
    }

    public Integer getAmount() {
        return values.size();
    }

    public HashMap<String, String> getValues() {
        return values;
    }

}
