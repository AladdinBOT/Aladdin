package net.heyzeer0.aladdin.profiles;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import okhttp3.OkHttpClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WebhookProfile {

    public static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    String name;
    String image;
    TextChannel ch;

    Webhook wh;
    SimpleWebhook client;

    public WebhookProfile(String name, String image, TextChannel ch) {
        this.name = name;
        this.image = image;
        this.ch = ch;

        ch.getWebhooks().complete().forEach(wb -> {
            if(wb.getName().equalsIgnoreCase(name)) {
                wh = wb;
                return;
            }
        });

        if(wh == null) {
            wh = ch.createWebhook(name).complete();
        }

        client = new SimpleWebhook(wh.getIdLong(), wh.getToken(), httpClient, service);
    }

    public void sendMessage(String x) {
        client.send(new WebhookMessageBuilder().setUsername(name).setAvatarUrl(image).setContent(x).build());
    }

    public void sendMessage(EmbedBuilder b) {
        client.send(new WebhookMessageBuilder().setUsername(name).setAvatarUrl(image).addEmbeds(b.build()).build());
    }

    private static class SimpleWebhook extends WebhookClient {

        SimpleWebhook(long id, String token, OkHttpClient client, ScheduledExecutorService service) {
            super(id, token, client, service);
        }

    }

}
