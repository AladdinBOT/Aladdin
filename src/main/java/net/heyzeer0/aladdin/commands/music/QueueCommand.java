package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.instances.GuildController;
import net.heyzeer0.aladdin.music.instances.MusicContext;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import net.heyzeer0.aladdin.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 06/08/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class QueueCommand implements CommandExecutor {

    @Command(command = "queue", description = "command.music.queue.description", type = CommandType.MUSIC,
            usage = "a!queue")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(!Main.getMusicManager().isConnected(e.getGuild())) {
            e.sendMessage(lp.get("command.music.queue.error.notplaying"));
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        GuildController controller = Main.getMusicManager().getGuildController(e.getGuild());
        MusicContext current = controller.getCurrentTrack();

        if(controller.getQueue().size() <= 0) {
            e.sendPureMessage(
                    lp.get("command.music.queue.success", current.getAudioTrack().getInfo().title, Utils.format(controller.getPlayer().getPlayingTrack().getPosition()), Utils.format(current.getAudioTrack().getDuration()))
            ).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return new CommandResult((CommandResultEnum.SUCCESS));
        }



        ArrayList<MusicContext> tracks = new ArrayList<>(controller.getQueue());
        Paginator ph = new Paginator(e, lp.get("command.music.queue.paginator.title"));

        int pages = (tracks.size() + (10 + 1)) / 10;


        Integer actual = 0;
        Integer pactual = 1;

        for (int i = 1; i <= pages; i++) {
            String pg = lp.get("command.music.queue.paginator.page.current", current.getAudioTrack().getInfo().title, Utils.format(controller.getPlayer().getPlayingTrack().getPosition()), Utils.format(current.getAudioTrack().getDuration())) + "\n";
            for (int p = actual; p < pactual * 10; p++) {
                if (tracks.size() <= p) {
                    break;
                }
                actual++;

                MusicContext track = tracks.get(p);
                pg = pg + lp.get("command.music.queue.paginator.page.normal", p, track.getAudioTrack().getInfo().title, Utils.format(track.getAudioTrack().getDuration()), track.getDJasMember().getEffectiveName()) + "\n";
            }
            pactual++;
            ph.addPage(pg);
        }

        ph.start();

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
