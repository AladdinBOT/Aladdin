package net.heyzeer0.aladdin.profiles.custom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by HeyZeer0 on 25/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MojangProfile {

    public static final String server_api = "https://mcapi.ca/mcstatus";

    HashMap<String, String> server_status = new HashMap<>();

    public MojangProfile() {
        try{
            URL url = new URL(server_api);
            InputStream in = url.openStream();
            Scanner scan = new Scanner(in);
            String jsonstring = "";
            while(scan.hasNext()){
                jsonstring += scan.next() + " ";
            }
            scan.close();

            Gson gson = new GsonBuilder().create();

            JsonObject json = gson.fromJson(jsonstring, JsonElement.class).getAsJsonObject();

            server_status.put("minecraft.net", json.getAsJsonObject("minecraft.net").get("status").getAsString());
            server_status.put("mojang.api", json.getAsJsonObject("mojang.com").get("status").getAsString());
            server_status.put("Session", json.getAsJsonObject("session.minecraft.net").get("status").getAsString());
            server_status.put("Account", json.getAsJsonObject("account.mojang.com").get("status").getAsString());
            server_status.put("Auth", json.getAsJsonObject("auth.mojang.com").get("status").getAsString());
            server_status.put("Skins", json.getAsJsonObject("skins.minecraft.net").get("status").getAsString());
            server_status.put("API", json.getAsJsonObject("api.mojang.com").get("status").getAsString());
            server_status.put("Textures", json.getAsJsonObject("textures.minecraft.net").get("status").getAsString());

        }catch (Exception e) {}
    }

    public void sendAsEmbed(MessageEvent e, LangProfile lp) {
        EmbedBuilder b = new EmbedBuilder().setColor(Color.GREEN)
                .setDescription(lp.get("command.minecraft.status.embed.title"))
                .setTitle("Mojang Status")
                .setThumbnail("https://qph.ec.quoracdn.net/main-qimg-7607deb0a45b46cd9609bb800a58c9d9")
                .setFooter(String.format(lp.get("command.minecraft.status.embed.footer"), e.getAuthor().getName()), e.getAuthor().getAvatarUrl())
                .setTimestamp(e.getMessage().getCreationTime());

        for(String x : server_status.keySet()) {
            b.addField(x, server_status.get(x), true);
        }

        e.sendMessage(b);
    }



}
