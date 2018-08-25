package net.heyzeer0.aladdin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import net.heyzeer0.aladdin.commands.*;
import net.heyzeer0.aladdin.commands.music.*;
import net.heyzeer0.aladdin.configs.ConfigManager;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.configs.instances.DatabaseConfig;
import net.heyzeer0.aladdin.database.AladdinData;
import net.heyzeer0.aladdin.enums.Lang;
import net.heyzeer0.aladdin.manager.commands.CommandManager;
import net.heyzeer0.aladdin.manager.custom.GiveawayManager;
import net.heyzeer0.aladdin.manager.custom.ReminderManager;
import net.heyzeer0.aladdin.manager.custom.osu.OsuSubscriptionManager;
import net.heyzeer0.aladdin.manager.custom.warframe.SubscriptionManager;
import net.heyzeer0.aladdin.manager.utilities.ChooserManager;
import net.heyzeer0.aladdin.manager.utilities.PaginatorManager;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.profiles.LogProfile;
import net.heyzeer0.aladdin.profiles.ShardProfile;
import net.heyzeer0.aladdin.utils.DiscordLists;
import net.heyzeer0.aladdin.utils.Utils;

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
    private static MusicManager musicManager;

    public static String version = "2.0.1";

    public static void main(String args[]) {
        try{
            getDataFolder().mkdirs();

            RestAction.setPassContext(true);
            RestAction.DEFAULT_FAILURE = null;

            logger.startMsCount();
            ConfigManager.lockAndLoad(DatabaseConfig.class);
            ConfigManager.lockAndLoad(BotConfig.class);
            ConfigManager.lockAndLoad(ApiKeysConfig.class);

            if(BotConfig.bot_token.equalsIgnoreCase("<insert-here>") || DatabaseConfig.rethink_ip.equalsIgnoreCase("<insert-here>")) {
                logger.warn("You need to setup configurations first to start the bot.");
                return;
            }
            logger.finishMsCount("Configs");

            logger.startMsCount();
            int shard_amount = Utils.getShardAmount();

            musicManager = new MusicManager(shard_amount, BotConfig.bot_id);
            logger.finishMsCount("Music");

            logger.startMsCount();
            shards = new ShardProfile[shard_amount];
            for(int i = 0; i < shards.length; i++) {
                if(i != 0) Thread.sleep(5000);
                shards[i] = new ShardProfile(i, shards.length, musicManager);
            }
            logger.finishMsCount("Shards");

            logger.startMsCount();
            data = new AladdinData();
            logger.finishMsCount("Database");

            logger.startMsCount();
            for(Lang l : Lang.values()) {
                l.getLangProfile();
            }
            logger.finishMsCount("Langs");

            logger.startMsCount();
            CommandManager.registerCommand(new AdminCommand());
            CommandManager.registerCommand(new AkinatorCommand());
            CommandManager.registerCommand(new BotCommand());
            CommandManager.registerCommand(new ChatClearCommand());
            CommandManager.registerCommand(new CommandsCommand());
            CommandManager.registerCommand(new ConfigCommand());
            CommandManager.registerCommand(new EmojiCommand());
            CommandManager.registerCommand(new GiveawayCommand());
            CommandManager.registerCommand(new GroupCommand());
            CommandManager.registerCommand(new HelpCommand());
            CommandManager.registerCommand(new IamCommand());
            CommandManager.registerCommand(new ImageCommand());
            CommandManager.registerCommand(new InviteCommand());
            CommandManager.registerCommand(new JavaCommand());
            CommandManager.registerCommand(new LeagueOfLegendsCommand());
            CommandManager.registerCommand(new LogCommand());
            CommandManager.registerCommand(new MathCommand());
            CommandManager.registerCommand(new MinecraftCommand());
            CommandManager.registerCommand(new OsuCommand());
            CommandManager.registerCommand(new OverwatchCommand());
            CommandManager.registerCommand(new PaladinsCommand());
            CommandManager.registerCommand(new PremiumCommand());
            CommandManager.registerCommand(new ReminderCommand());
            CommandManager.registerCommand(new StarboardCommand());
            CommandManager.registerCommand(new UserCommand());
            CommandManager.registerCommand(new WarframeCommand());
            CommandManager.registerCommand(new WeatherCommand());

            CommandManager.registerCommand(new LyricsCommand());
            CommandManager.registerCommand(new PlayCommand());
            CommandManager.registerCommand(new StopCommand());
            CommandManager.registerCommand(new SkipCommand());
            CommandManager.registerCommand(new RepeatCommand());
            CommandManager.registerCommand(new QueueCommand());
            CommandManager.registerCommand(new VolumeCommand());

            logger.finishMsCount("Commands");

            logger.info("\n    ___    __          __    ___     \n" +
                    "   /   |  / /___ _____/ /___/ (_)___ \n" +
                    "  / /| | / / __ `/ __  / __  / / __ \\\n" +
                    " / ___ |/ / /_/ / /_/ / /_/ / / / / /\n" +
                    "/_/  |_/_/\\__,_/\\__,_/\\__,_/_/_/ /_/ \n" +
                    "                         v" + version);


            checkThreads();
            ChooserManager.startCleanup();
            PaginatorManager.startCleanup();
            DiscordLists.updateStatus();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void checkThreads() {
        if(BotConfig.dev.equalsIgnoreCase("true")) return;

        ThreadManager.startThread(true);
        GiveawayManager.startUpdating();
        SubscriptionManager.startUpdating();
        ReminderManager.startChecking();
        OsuSubscriptionManager.startUpdating();
    }

    public static File getDataFolder() {
        return new File(System.getProperty("user.dir"), "data");
    }

    public static LogProfile getLogger() { return logger; }

    public static AladdinData getDatabase() {
        return data;
    }

    public static MusicManager getMusicManager() {
        return musicManager;
    }

    public static ShardProfile[] getShards() {
        return shards;
    }

    public static ShardProfile[] getConnectedShards() {
        return Stream.of(shards).filter(shard -> shard.getJDA().getStatus() == JDA.Status.CONNECTED).toArray(ShardProfile[]::new);
    }

    public static Guild getGuildById(String id) {
        for(ShardProfile s : getConnectedShards()) {
            if(s.getJDA().getGuildById(id) != null) {
                return s.getJDA().getGuildById(id);
            }
        }
        return null;
    }

    public static ShardProfile getShard(int id) {
        return Arrays.stream(shards).filter(shard -> shard.getShardId() == id).findFirst().orElse(null);
    }

    public static ShardProfile getShardForGuild(long guildId) {
        return getShard((int) ((guildId >> 22) % getShards().length));
    }

    public static User getUserById(long id) {
        for(ShardProfile pf : getConnectedShards()) {
            if(pf.getJDA().getUserById(id) != null) {
                return pf.getJDA().getUserById(id);
            }
        }
        return null;
    }

    public static User getUserById(String id) {
        for(ShardProfile pf : getConnectedShards()) {
            if(pf.getJDA().getUserById(id) != null) {
                return pf.getJDA().getUserById(id);
            }
        }
        return null;
    }

    public static List<Guild> getMutualGuilds(User user) {
        ArrayList<Guild> guilds = new ArrayList<>();
        Stream.of(getConnectedShards()).forEach(sp -> guilds.addAll(sp.getJDA().getMutualGuilds(user)));
        return guilds;
    }

    public static TextChannel getTextChannelById(String id) {
        ShardProfile sh = Stream.of(getConnectedShards()).filter(s -> s.getJDA().getTextChannelById(id) != null).findFirst().orElse(null);
        return sh == null ? null : sh.getJDA().getTextChannelById(id);
    }

}
