package net.heyzeer0.aladdin.configs;

import net.heyzeer0.aladdin.interfaces.annotation.YamlConfig;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */

@YamlConfig(name = "main", folder = "configs")
public class MainConfig {

    public static String bot_token = "<insert-here>";
    public static String bot_game = "Tudo esta conectado";
    public static String rethink_ip = "<insert-here>";
    public static int rethink_port = 28015;
    public static String rethink_user = "<insert-here>";
    public static String rethink_pass = "<insert-here>";
    public static String rethink_db = "<insert-here>";
    public static String bot_owner = "169904764048375809";
    public static String bot_guildlog_id = "<insert-here>";

}
