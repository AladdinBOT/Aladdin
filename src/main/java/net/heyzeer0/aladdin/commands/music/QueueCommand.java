package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;
import net.heyzeer0.aladdin.music.profiles.PlayerContext;
import net.heyzeer0.aladdin.music.utils.AudioUtils;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class QueueCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "queue", description = "Veja as musicas na queue", aliasses = {"q"}, type = CommandType.MUSIC,
            usage = "a!queue")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        GuildTrackProfile guild = MusicManager.getManager(e.getGuild());

        if(guild.getQueue().size() <= 0) {
            if(guild.getCurrentTrack() != null) {
                e.sendPureMessage(":musical_note: Música atual: ``" + guild.getCurrentTrack().getTrack().getInfo().title + " (" + AudioUtils.format(guild.getCurrentTrack().getTrack().getPosition()) + "/" + AudioUtils.format(guild.getCurrentTrack().getTrack().getDuration()) + ")``").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            e.sendMessage(EmojiList.WORRIED + " Oops, não há músicas na playlist ^o^");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        PlayerContext current = guild.getCurrentTrack();

        ArrayList<PlayerContext> tracks = new ArrayList<>(guild.getQueue());
        Paginator ph = new Paginator(e, ":musical_note: Listando as músicas na playlist!");

        int pages = (tracks.size() + (10 + 1)) / 10;


        Integer actual = 0;
        Integer pactual = 1;


        for (int i = 1; i <= pages; i++) {
            String pg = "Atual: " + current.getTrack().getInfo().title + " (" + AudioUtils.format(current.getTrack().getPosition()) + "/" + AudioUtils.format(current.getTrack().getDuration()) + ")\n";
            for (int p = actual; p < pactual * 10; p++) {
                if (tracks.size() <= p) {
                    break;
                }
                actual++;
                pg = pg + "#" + p + " Nome: " +  tracks.get(p).getTrack().getInfo().title + "(" + AudioUtils.format(tracks.get(p).getTrack().getDuration()) + ") | Pedinte: " + tracks.get(p).getDJ().getName() + "\n";
            }
            pactual++;
            ph.addPage(pg);
        }

        ph.start();
        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
