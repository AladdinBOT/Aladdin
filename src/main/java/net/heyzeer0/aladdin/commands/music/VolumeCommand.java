package net.heyzeer0.aladdin.commands.music;

import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.MusicManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class VolumeCommand implements CommandExecutor {

    @Command(command = "volume", description = "Altere o volume do player de musica", parameters = {"30 a 110"}, type = CommandType.MUSIC,
            usage = "a!volume 30", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(!e.getUserProfile().isPremiumActive()) {
            return new CommandResult((CommandResultEnum.NEED_PREMIUM));
        }

        try{
            Integer value = Integer.valueOf(args.get(0));

            if(!e.getAuthor().getId().equals(BotConfig.bot_owner)) {
                if(value < 30 || value > 110) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o valor necessita ser maior que ``30`` e menor que ``110``");
                    return new CommandResult((CommandResultEnum.SUCCESS));
                }
            }

            if (e.getGuild().getAudioManager().isConnected() && !e.getGuild().getAudioManager().getConnectedChannel().equals(e.getMember().getVoiceState().getChannel())) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não esta conectado ao meu canal de audio!");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            if(!e.getGuild().getAudioManager().isConnected()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, não ha nenhum player de musica ativo, use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "play [nome]`` para adicionar algo a queue.");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }
            if(!e.getMember().getVoiceState().inVoiceChannel()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não esta conectado a um canal de voz!");
                return new CommandResult((CommandResultEnum.SUCCESS));
            }

            MusicManager.getManager(e.getGuild()).getAudioPlayer().setVolume(value);

            e.sendMessage(EmojiList.CORRECT + " Você alterou o volume do player atual para ``" + value + "%``");
        }catch (Exception ex) {
            e.sendMessage(EmojiList.WORRIED + " Oops, o valor inserido é invalido, necessita ser um número de ``30`` a ``110``");
        }

        return new CommandResult((CommandResultEnum.SUCCESS));
    }

}
