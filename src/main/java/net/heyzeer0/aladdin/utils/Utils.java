package net.heyzeer0.aladdin.utils;

import com.google.gson.JsonParser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.database.entities.profiles.GroupProfile;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.enums.LogModules;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.manager.commands.CommandManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Utils {

    public static Random r = new Random();
    private static final Map<String, String> regional = new HashMap<>();
    private static ScheduledExecutorService async = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService timers = Executors.newSingleThreadScheduledExecutor();

    public static void runAsync(Runnable r) {
        async.execute(r);
    }

    public static void runLater(Runnable r, long time, TimeUnit unit) {
        timers.schedule(r, time, unit);
    }

    public static void runTimer(Runnable r, long delay, TimeUnit unit) {
        timers.scheduleAtFixedRate(r, 0, delay, unit);
    }

    public static int getShardAmmount() {
        try {
            return new Router("https://discordapp.com/api/gateway/bot")
                    .addHeaderParameter("Authorization", "Bot " + BotConfig.bot_token)
                    .addHeaderParameter("Content-Typw", "application/json")
                    .getResponse().asJsonObject().getInt("shards");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String translateTempo(String entrada) {
        if(entrada.equalsIgnoreCase("Mostly Cloudy")) {
            return "Predominantemente nublado";
        }
        if(entrada.equalsIgnoreCase("Partly Cloudy")) {
            return "Parcialmente nublado";
        }
        if(entrada.equalsIgnoreCase("Showers")) {
            return "Aguaceiros";
        }
        if(entrada.equalsIgnoreCase("Cloudy")) {
            return "Nublado";
        }
        if(entrada.equalsIgnoreCase("Sunny")) {
            return "Ensolarado";
        }
        if(entrada.equalsIgnoreCase("Breezy")) {
            return "Ventoso";
        }
        if(entrada.equalsIgnoreCase("Clear")) {
            return "Limpo";
        }
        if(entrada.equalsIgnoreCase("Thunderstorms")) {
            return "Trovoadas";
        }
        if(entrada.equalsIgnoreCase("Rain")) {
            return "Chovendo";
        }
        if(entrada.equalsIgnoreCase("Mostly Sunny")) {
            return "Maioritariamente Ensolarado";
        }

        return entrada;
    }

    public static String sendToHastebin(String data) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://hastebin.com/documents");

        try {
            post.setEntity(new StringEntity(data));

            HttpResponse response = client.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            return "https://hastebin.com/" + new JsonParser().parse(result).getAsJsonObject().get("key").getAsString();
        } catch (IOException e) {
            Main.getLogger().warn(data);
        }
        return data;
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String convertToBar(long maximo, long atual) {
        double maxHealth = maximo;
        double currentHealth = Math.max(atual, 0);
        double healthPercentage = (currentHealth / maxHealth) * 100.0D;

        String spacer = "\u2588";

        int coloredDisplay = (int) Math.ceil(20 * (healthPercentage / 100.0D));

        String healthbar = "";

        for (int i = 0; i < 20; i++) {
            if (coloredDisplay > 0) {
                healthbar += spacer;
                coloredDisplay--;
            } else {
                healthbar += " ";
            }
        }

        healthbar = "[" + healthbar + "]";

        return healthbar;
    }

    public static String getTime(long duration) {
        final long
                years = duration / 31104000000L,
                months = duration / 2592000000L % 12,
                days = duration / 86400000L % 30,
                hours = duration / 3600000L % 24,
                minutes = duration / 60000L % 60,
                seconds = duration / 1000L % 60;
        String uptime = (years == 0 ? "" : years + " Ano" + cS(years) + ", ") + (months == 0 ? "" : months + " Mes" + cS(months, true) + ", ")
                + (days == 0 ? "" : days + " Dia" + cS(days) + ", ") + (hours == 0 ? "" : hours + " Hora" + cS(hours) + ", ")
                + (minutes == 0 ? "" : minutes + " Minuto" + cS(minutes) + ", ") + (seconds == 0 ? "" : seconds + " Segundo" + cS(seconds) + ", ");

        uptime = replaceLast(uptime, ", ", "");
        uptime = replaceLast(uptime, ",", " e");

        return uptime;
    }

    public static String cS(long value) {
        if(value == 1) {
            return "";
        }
        return "s";
    }

    public static String cS(long value, boolean e) {
        if(value == 1) {
            return "";
        }
        return e ? "es" : "s";
    }

    public static String toMD5(String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(input));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        }catch (Exception ex) {}

        return input;
    }


    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static String cryptWithMD5(String value){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = value.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getRegional(String value) {
        if(regional.size() <= 0) {
            regional.put("0", "0⃣");
            regional.put("1", "1⃣");
            regional.put("2", "2⃣");
            regional.put("3", "3⃣");
            regional.put("4", "4⃣");
            regional.put("5", "5⃣");

            regional.put("6", "6⃣");
            regional.put("7", "7⃣");
            regional.put("8", "8⃣");
            regional.put("9", "9⃣");

            regional.put("0⃣", "0");
            regional.put("1⃣", "1");
            regional.put("2⃣", "2");
            regional.put("3⃣", "3");
            regional.put("3⃣", "4");
            regional.put("5⃣", "5");
            regional.put("6⃣", "6");
            regional.put("7⃣", "7");
            regional.put("8⃣", "8");
            regional.put("9⃣", "9");
        }
        return regional.getOrDefault(value, null);
    }

    public static List<String> readBuffer(String link) {
        BufferedReader in = null;
        List<String> raw = new ArrayList<>();
        try {
            URL url = new URL(link);

            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                raw.add(str);
            }
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {}
            }
        }
        return raw;
    }

    public static HashMap<GuildConfig, Object> getDefaultValues() {
        HashMap<GuildConfig, Object> values = new HashMap<>();
        for (GuildConfig guildConfig : GuildConfig.values()) {
            values.put(guildConfig, guildConfig.getDefault());
        }
        return values;
    }

    public static HashMap<LogModules, Boolean> getLogDefaults() {
        HashMap<LogModules, Boolean> values = new HashMap<>();
        for (LogModules guildConfig : LogModules.values()) {
            values.put(guildConfig, guildConfig.isActive());
        }
        return values;
    }

    public static MessageReaction findReaction(String emote, String id, Message msg) {
        if(msg.getReactions().size() <= 0) {
            return null;
        }

        for(MessageReaction rc : msg.getReactions()) {
            if(rc.getReactionEmote().getName().equals(emote)) {
                if(id == null) {
                    return rc;
                }
                if(rc.getReactionEmote().getId().equals(id)) {
                    return rc;
                }
            }
        }

        return null;
    }

    public static HashMap<String, GroupProfile> getDefaultGroup() {
        ArrayList<String> permissions = new ArrayList<>();
        for(String x : CommandManager.commands.keySet()) {
            Command cmd = CommandManager.commands.get(x).getAnnotation();
            if(cmd.isAllowedToDefault()) {
                permissions.add("command." + x);
            }
        }

        GroupProfile pf = new GroupProfile("everyone", permissions, true);
        pf.setDefault(true);

        HashMap<String, GroupProfile> gp = new HashMap<>();
        gp.put("everyone", pf);

        return gp;
    }

}
