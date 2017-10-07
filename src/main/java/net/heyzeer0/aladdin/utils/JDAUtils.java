package net.heyzeer0.aladdin.utils;

import com.mashape.unirest.http.Unirest;
import net.heyzeer0.aladdin.configs.MainConfig;

/**
 * Created by HeyZeer0 on 05/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class JDAUtils {

    public static int getShardAmmount() {
        try {
            return Unirest.get("https://discordapp.com/api/gateway/bot")
                    .header("Authorization", "Bot " + MainConfig.bot_token)
                    .header("Content-Type", "application/json")
                    .asJson()
                    .getBody().getObject().getInt("shards");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
