package net.heyzeer0.aladdin.utils;

import net.heyzeer0.aladdin.Main;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    String post;
    boolean image = false;

    public Router(String address) {
        this.address = address;

        header_parameters.put("User-Agent", "Aladdin BOT " + Main.version + " - JDA - Discord BOT");
    }

    public Router addHeaderParameter(String key, String value) {
        header_parameters.put(key, value);

        return this;
    }

    public Router addUrlParameters(String key, Object value) {
        url_parameters.put(key, value.toString());

        return this;
    }

    public Router setPostRequest(Object obj) {
        post = obj.toString();

        return this;
    }

    public Router acceptImage() {
        image = true;

        return this;
    }

    public Response getResponse() throws Exception {
        return new Response();
    }

    public class Response {

        String result;
        BufferedImage img;

        public Response() throws Exception {
            String url = address;

            if(url_parameters.size() > 0) {
                boolean first = true;
                for(String key : url_parameters.keySet()) {
                    if(first) {
                        first = false;
                        url = url + "?" + key + "=" + url_parameters.get(key);
                        continue;
                    }

                    url = url + "&" + key + "=" + url_parameters.get(key);
                }
            }

            URLConnection st = new URL(url).openConnection();
            for(String k : header_parameters.keySet()) {
                st.setRequestProperty(k, header_parameters.get(k));
            }

            if(post != null) {
                st.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(st.getOutputStream());
                dos.writeBytes(post);
                dos.flush();
                dos.close();
            }

            if(image) {
                InputStream istream;
                try {
                    istream = st.getInputStream();

                    img = ImageIO.read(istream);
                } catch (IOException ignored) { }
            }

            result = IOUtils.toString(st.getInputStream());
        }

        public JSONObject asJsonObject() {
            return new JSONObject(result);
        }

        public JSONArray asJsonArray() {
            return new JSONArray(result);
        }

        public BufferedImage asImage() {
            return img;
        }

        public String getResult() {
            return result;
        }

    }

}
