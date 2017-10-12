package net.heyzeer0.aladdin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.commands.*;
import net.heyzeer0.aladdin.commands.music.*;
import net.heyzeer0.aladdin.configs.ApiKeysConfig;
import net.heyzeer0.aladdin.configs.MainConfig;
import net.heyzeer0.aladdin.database.AladdinData;
import net.heyzeer0.aladdin.manager.ConfigManager;
import net.heyzeer0.aladdin.manager.commands.CommandManager;
import net.heyzeer0.aladdin.manager.utilities.ChooserManager;
import net.heyzeer0.aladdin.manager.utilities.PaginatorManager;
import net.heyzeer0.aladdin.profiles.LogProfile;
import net.heyzeer0.aladdin.profiles.ShardProfile;
import net.heyzeer0.aladdin.profiles.SocketInfo;
import net.heyzeer0.aladdin.utils.JDAUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Main {

    private static LogProfile logger = new LogProfile("Main");
    private static ShardProfile[] shards;
    private static AladdinData data;

    public static String version = "0.1";

    public static void main(String args[]) {
        try{
            ConfigManager.lockAndLoad(MainConfig.class);
            ConfigManager.lockAndLoad(ApiKeysConfig.class);

            if(MainConfig.bot_token.equalsIgnoreCase("<insert-here>")) {
                logger.warn("Para iniciar o bot você precisa definir o token na configuração.");
                return;
            }
            if(MainConfig.rethink_ip.equalsIgnoreCase("<insert-here>")) {
                logger.warn("Para iniciar o bot você precisa definir a configuração da database.");
                return;
            }

            long shard = System.currentTimeMillis();

            shards = new ShardProfile[JDAUtils.getShardAmmount()];
            for(int i = 0; i < shards.length; i++) {
                if(i != 0) Thread.sleep(5000);
                shards[i] = new ShardProfile(i, shards.length);
            }

            logger.info("Shards iniciadas em " + (System.currentTimeMillis() - shard) + "ms");

            long database = System.currentTimeMillis();
            data = new AladdinData();

            logger.info("Database iniciada em " + (System.currentTimeMillis() - database) + "ms");

            long commands = System.currentTimeMillis();

            CommandManager.registerCommand(new AdminCommand());
            CommandManager.registerCommand(new BotCommand());
            CommandManager.registerCommand(new ChatClearCommand());
            CommandManager.registerCommand(new CommandsCommand());
            CommandManager.registerCommand(new ConfigCommand());
            CommandManager.registerCommand(new EmojiCommand());
            CommandManager.registerCommand(new GoogleCommand());
            CommandManager.registerCommand(new GroupCommand());
            CommandManager.registerCommand(new HelpCommand());
            CommandManager.registerCommand(new InviteCommand());
            CommandManager.registerCommand(new JavaCommand());
            CommandManager.registerCommand(new MathCommand());
            CommandManager.registerCommand(new MinecraftCommand());
            CommandManager.registerCommand(new OsuCommand());
            CommandManager.registerCommand(new OverwatchCommand());
            CommandManager.registerCommand(new PaladinsCommand());
            CommandManager.registerCommand(new PremiumCommand());
            CommandManager.registerCommand(new UserCommand());
            CommandManager.registerCommand(new WarframeCommand());
            CommandManager.registerCommand(new WeatherCommand());

            CommandManager.registerCommand(new PlayCommand());
            CommandManager.registerCommand(new PlaylistCommand());
            CommandManager.registerCommand(new QueueCommand());
            CommandManager.registerCommand(new RepeatCommand());
            CommandManager.registerCommand(new SkipCommand());
            CommandManager.registerCommand(new StopCommand());

            logger.info("Comandos registrados em " + (System.currentTimeMillis() - commands) + "ms");

            logger.info("\n    ___    __          __    ___     \n" +
                    "   /   |  / /___ _____/ /___/ (_)___ \n" +
                    "  / /| | / / __ `/ __  / __  / / __ \\\n" +
                    " / ___ |/ / /_/ / /_/ / /_/ / / / / /\n" +
                    "/_/  |_/_/\\__,_/\\__,_/\\__,_/_/_/ /_/ \n" +
                    "                         v" + version);

            ChooserManager.startCleanup();
            PaginatorManager.startCleanup();

            new SocketInfo(9598, (l, i) -> {
                if(l.equalsIgnoreCase("shutdown")) {
                    i.shutdown();
                    Stream.of(Main.getShards()).forEach(sc -> sc.getJDA().shutdown());
                    System.exit(0);
                }
            });

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }

    public static LogProfile getLogger() { return logger; }

    public static AladdinData getDatabase() {
        return data;
    }

    public static ShardProfile[] getShards() {
        return shards;
    }

    public static ShardProfile[] getConnectedShards() {
        return Stream.of(shards).filter(shard -> shard.getJDA().getStatus() == JDA.Status.CONNECTED).toArray(ShardProfile[]::new);
    }

    public static ShardProfile getShard(int id) {
        return Arrays.stream(shards).filter(shard -> shard.getShardId() == id).findFirst().orElse(null);
    }

    public static ShardProfile getShardForGuild(long guildId) {
        return getShard((int) ((guildId >> 22) % getShards().length));
    }

    public static List<Guild> getMutualGuilds(User user) {
        ArrayList<Guild> guilds = new ArrayList<>();
        Stream.of(getConnectedShards()).forEach(sp -> guilds.addAll(sp.getJDA().getMutualGuilds(user)));
        return guilds;
    }

    public static TextChannel getTextChannelById(String id) {
        TextChannel ch = null;
        for(ShardProfile shards : getConnectedShards()) {
            if(ch != null) {
                break;
            }
            if(shards.getJDA().getTextChannelById(id) != null) {
                ch = shards.getJDA().getTextChannelById(id);
            }
        }

        return ch;
    }
}
