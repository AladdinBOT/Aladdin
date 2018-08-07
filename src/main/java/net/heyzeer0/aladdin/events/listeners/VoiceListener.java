package net.heyzeer0.aladdin.events.listeners;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class VoiceListener {

    private static final ScheduledExecutorService leaveExecutor = Executors.newScheduledThreadPool(5, r -> { Thread t = new Thread(r, "MusicLeaveExecutor"); t.setDaemon(true); return t; });
    private static final HashMap<String, ScheduledFuture<?>> concurrentTasks = new HashMap<>();

    public static void onVoiceLeave(VoiceChannel vc) {
        if(!Main.getMusicManager().isConnected(vc.getGuild())) {
            return;
        }

        if(Utils.isAlone(vc) && ! concurrentTasks.containsKey(vc.getId())) {
            GuildController controller = Main.getMusicManager().getGuildController(vc.getGuild());
            controller.getPlayer().setPaused(true);

            ScheduledFuture<?> executor = leaveExecutor.schedule(() -> {
                Main.getMusicManager().getGuildController(vc.getGuild()).queueFinish();
                concurrentTasks.remove(vc.getId());
            }, 1, TimeUnit.MINUTES);

            controller.deleteAndCreateMessage(controller.getLangProfile().get("music.leftalone"));
            concurrentTasks.put(vc.getId(), executor);
        }
    }

    public static void onVoiceJoin(VoiceChannel vc) {
        if(concurrentTasks.containsKey(vc.getId())) {
            if(!Main.getMusicManager().isConnected(vc.getGuild())) {
                concurrentTasks.get(vc.getId()).cancel(true);
                concurrentTasks.remove(vc.getId());
                return;
            }

            GuildController controller = Main.getMusicManager().getGuildController(vc.getGuild());
            controller.getPlayer().setPaused(false);

            concurrentTasks.get(vc.getId()).cancel(true);
            concurrentTasks.remove(vc.getId());
        }
    }

}
