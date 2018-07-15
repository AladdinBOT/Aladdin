package net.heyzeer0.aladdin.profiles.custom;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Router;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 25/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MojangProfile {

    public static final String server_api = "https://mcapi.ca/mcstatus";

    HashMap<String, String> server_status = new HashMap<>();

    public MojangProfile() {
        try{

            JSONObject json = new Router(server_api).getResponse().asJsonObject();

            server_status.put("minecraft.net", json.getJSONObject("minecraft.net").getString("status"));
            server_status.put("minecraft.net Session", json.getJSONObject("session.minecraft.net").getString("status"));
            server_status.put("mojang.com", json.getJSONObject("mojang.com").getString("status"));
            server_status.put("Session", json.getJSONObject("sessionserver.mojang.net").getString("status"));
            server_status.put("Account", json.getJSONObject("account.mojang.com").getString("status"));
            server_status.put("Auth", json.getJSONObject("authserver.mojang.com").getString("status"));
            server_status.put("Skins", json.getJSONObject("textures.minecraft.net").getString("status"));
            server_status.put("API", json.getJSONObject("api.mojang.com").getString("status"));
            server_status.put("Textures", json.getJSONObject("textures.minecraft.net").getString("status"));

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
