package net.heyzeer0.aladdin.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 23/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Router {

    String address;
    HashMap<String, String> header_parameters = new HashMap<>();
    HashMap<String, String> url_parameters = new HashMap<>();

    public Router(String address) {
        this.address = address;

        header_parameters.put("User-Agent", "Aladdin BOT 1.5 - JDA - Discord BOT");
    }

    public Router addHeaderParameter(String key, String value) {
        header_parameters.put(key, value);

        return this;
    }

    public Router addUrlParameters(String key, Object value) {
        url_parameters.put(key, value.toString());

        return this;
    }

    public Response getResponse() throws Exception {
        return new Response(this);
    }

    public class Response {

        String result;

        public Response(Router r) throws Exception {
            String url = r.address;

            if(r.url_parameters.size() > 0) {
                boolean first = true;
                for(String key : r.url_parameters.keySet()) {
                    if(first) {
                        first = false;
                        url = url + "?" + key + "=" + r.url_parameters.get(key);
                        continue;
                    }

                    url = url + "&" + key + "=" + r.url_parameters.get(key);
                }
            }

            URLConnection st = new URL(url).openConnection();
            for(String k : r.header_parameters.keySet()) {
                st.setRequestProperty(k, r.header_parameters.get(k));
            }

            result = IOUtils.toString(st.getInputStream());
        }

        public JSONObject asJsonObject() {
            return new JSONObject(result);
        }

        public JSONArray asJsonArray() {
            return new JSONArray(result);
        }

        public String getResult() {
            return result;
        }

    }

}
