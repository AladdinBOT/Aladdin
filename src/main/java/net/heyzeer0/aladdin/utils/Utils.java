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
import net.heyzeer0.aladdin.profiles.LangProfile;
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
    private static ScheduledExecutorService async = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService timers = Executors.newSingleThreadScheduledExecutor();

    public static void runAsync(Runnable r) {
        async.execute(r);
    }

    public static void runLater(Runnable r, long time, TimeUnit unit) {
        timers.schedule(r, time, unit);
    }

    public static int getShardAmount() {
        try {
            return new Router("https://discordapp.com/api/gateway/bot")
                    .addHeaderParameter("Authorization", "Bot " + BotConfig.bot_token)
                    .addHeaderParameter("Content-Type", "application/json")
                    .getResponse().asJsonObject().getInt("shards");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static <K,V> Map<K,V> createCache(final int maxSize) {
        return new LinkedHashMap<K,V>(maxSize*4/3, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public static String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }
        result[lastIndex] = s.substring(j);

        return result;
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

    public static String getTime(long duration, LangProfile lp) {
        final long
                years = duration / 31104000000L,
                months = duration / 2592000000L % 12,
                days = duration / 86400000L % 30,
                hours = duration / 3600000L % 24,
                minutes = duration / 60000L % 60,
                seconds = duration / 1000L % 60;
        String uptime = (years == 0 ? "" : years + " " + lp.get("command.bot.status.years") + ", ") + (months == 0 ? "" : months + " " + lp.get("command.bot.status.months") + ", ")
                + (days == 0 ? "" : days + " " + lp.get("command.bot.status.days") + ", ") + (hours == 0 ? "" : hours + " " + lp.get("command.bot.status.hours") + ", ")
                + (minutes == 0 ? "" : minutes + " " + lp.get("command.bot.status.minutes") + ", ") + (seconds == 0 ? "" : seconds + " " + lp.get("command.bot.status.seconds") + ", ");

        uptime = replaceLast(uptime, ", ", "");
        uptime = replaceLast(uptime, ",", " e");

        return uptime;
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

    public static String getRegional(String value) {
        switch(value) {
            case "0":
                return "0⃣";
            case "1":
                return "1⃣";
            case "2":
                return "2⃣";
            case "3":
                return "3⃣";
            case "4":
                return "4⃣";
            case "5":
                return "5⃣";
            case "6":
                return "6⃣";
            case "7":
                return "7⃣";
            case "8":
                return "8⃣";
            case "9":
                return "9⃣";
            default:
                return value.replace("⃣", "");
        }
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

    public static String format(long length) {
        long hours = length / 3600000L % 24,
                minutes = length / 60000L % 60,
                seconds = length / 1000L % 60;
        return (hours == 0 ? "" : octal(hours) + ":")
                + (minutes == 0 ? "00" : octal(minutes)) + ":" + (seconds == 0 ? "00" : octal(seconds));
    }

    public static String octal(long num) {
        if (num > 9) return String.valueOf(num);
        return "0" + num;
    }

}
