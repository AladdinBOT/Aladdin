package net.heyzeer0.aladdin.commands.music;

import gnu.trove.list.TLongList;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.profiles.GuildTrackProfile;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class SkipCommand implements CommandExecutor {

    //TODO lang
    @Command(command = "skip", description = "Pule a musica atual", aliasses = {"s"}, extra_perm = {"overpass"}, type = CommandType.MUSIC,
            usage = "a!skip")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        GuildTrackProfile guild = Main.getMusicManger().getManager(e.getGuild());

        if (e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().getConnectedChannel().equals(e.getMember().getVoiceState().getChannel())) {
            if(!e.hasPermission("command.skip.overpass")) {
                e.sendMessage(EmojiList.WORRIED + " Você não esta conectado ao meu canal!");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
        }

        if(guild.getCurrentTrack() == null && guild.getQueue().size() <= 0) {
            e.sendMessage(EmojiList.WORRIED + " Oops, não há músicas na playlist ^o^");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        if(e.hasPermission("command.skip.overpass")) {
            guild.skip();
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        TLongList voteSkips = guild.getVoteSkips();
        int requiredVotes = guild.getRequiredVotes();

        if (voteSkips.contains(e.getAuthor().getIdLong())) {
            voteSkips.remove(e.getAuthor().getIdLong());
            e.sendMessage(EmojiList.CORRECT + " Você retirou seu voto faltam mais " + (requiredVotes - voteSkips.size()) + " votos para pular!");
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        voteSkips.add(e.getAuthor().getIdLong());
        if (voteSkips.size() >= requiredVotes || guild.getCurrentTrack() != null && guild.getCurrentTrack().getDJ() != null && guild.getCurrentTrack().getDJ().getId().equals(e.getAuthor().getId())) {
            e.sendPureMessage(":musical_note: A quantidade de votos necessários para pular a música foi atiginda!").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            guild.skip();
            return new CommandResult((CommandResultEnum.SUCCESS));
        }

        e.sendPureMessage(EmojiList.CORRECT + " Voto contabilizado faltam mais " + (requiredVotes - voteSkips.size()) + " votos para pular!").queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
