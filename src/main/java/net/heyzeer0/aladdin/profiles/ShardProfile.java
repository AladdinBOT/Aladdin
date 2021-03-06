package net.heyzeer0.aladdin.profiles;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.events.EventControl;
import net.heyzeer0.aladdin.events.LogEvents;
import net.heyzeer0.aladdin.music.MusicManager;

import javax.security.auth.login.LoginException;

/**
 * Created by HeyZeer0 on 05/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ShardProfile {

    JDA jda;
    LogProfile logger;
    JDABuilder builder;

    boolean running = false;

    Integer shardId;
    long init_uptime;

    public ShardProfile(int shardid, int totalshards, MusicManager mg) {
        shardId = shardid;

        logger = new LogProfile("Shard " + shardid);
        builder = new JDABuilder(AccountType.BOT)
                .setToken(BotConfig.bot_token)
                .setAutoReconnect(true)
                .setAudioEnabled(true)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .setGame(Game.of(Game.GameType.DEFAULT, "a!help | " + BotConfig.bot_game + " [" + (shardid + 1) + "]"))
                .addEventListener(new EventControl(), new LogEvents(), mg.getLavaLink())
                .setCorePoolSize(10);

        builder.useSharding(shardid, totalshards);

        changeJDAStatus(false);
    }

    public void changeJDAStatus(boolean restart) {
        if(!running) {

            while(true) {
                try{
                    jda = builder.buildBlocking();
                    running = true;

                    init_uptime = System.currentTimeMillis();
                    break;
                }catch (LoginException e) {
                    logger.warn("Credenciais invalidas, irei desligar...");
                    System.exit(0);
                }catch (Exception e) {e.printStackTrace();}
            }

            return;
        }

        jda.shutdown();
        running = false;
        if(restart) {
            changeJDAStatus(false);
        }
    }

    public int getShardId() {
        return shardId;
    }

    public JDA getJDA() {
        return jda;
    }

    public long getInitUptime() {
        return init_uptime;
    }

}
