package net.heyzeer0.aladdin.commands.music;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.PlaylistTrackProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.music.profiles.AudioLoaderProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 02/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PlaylistCommand implements CommandExecutor {

    @Command(command = "playlist", description = "Crie playlists para auto-reprodução!", parameters = "criar/deletar/add/rem/play/list/info", type = CommandType.MUSIC,
            usage = "a!playlist criar Chillhop\na!playlist deletar Chillhop\na!playlist add Chillhop Brock Berrigan - Point Pleasant\na!playlist rem Chillhop 0\na!playlist play Chillhop\na!playlist list\na!playlist list @HeyZeer0\na!playlist info Chillhop")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("criar")) {

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "criar", "nome");
            }

            if(e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você já possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPlaylist().size() >= 2 && !e.getUserProfile().isPremiumActive()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você excedeu a quantidade maxima de playlists, você pode evitar isso adquirindo chaves premium.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getUserProfile().createPlaylist(args.get(1));

            e.sendMessage(EmojiList.CORRECT + " Você criou a playlist ``" + args.get(1) + "`` com sucesso.");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("deletar")) {

            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "deletar", "nome");
            }

            if(!e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getUserProfile().deletePlaylist(args.get(1));

            e.sendMessage(EmojiList.CORRECT + " Você deletou a playlist ``" + args.get(1) + "`` com sucesso.");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("add")) {

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "add", "playlist", "nome");
            }

            if(!e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPlaylist().get(args.get(1)).size() >= 20 && !e.getUserProfile().isPremiumActive()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você excedeu a quantidade maxima de musicas dessa playlist, você pode evitar isso adquirindo chaves premium.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.sendPureMessage(EmojiList.CORRECT + " Procurando por músicas com o seguinte argumento: ``" + args.getCompleteAfter(2) + "``").queue(msg -> AudioLoaderProfile.addToPlaylist(e.getAuthor(), msg, args.getCompleteAfter(2), false, args.get(1)));

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("rem")) {

            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "rem", "playlist", "id");
            }

            if(!e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            try{

                int x = Integer.valueOf(args.get(2));

                if(e.getUserProfile().getPlaylist().get(args.get(1)).size() < x) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, a sua playlist não possui musicas até a id ``" + x + "``");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                e.getUserProfile().removeTrackFromPlaylist(args.get(1), x);

                e.sendMessage(EmojiList.CORRECT + " Você removeu com sucesso a id ``" + x + "`` da playlist ``" + args.get(1) + "``");

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a **ID** inserida é invalido.");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("play")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "play", "playlist");
            }

            if (!e.getGuild().getMember(e.getAuthor()).getVoiceState().inVoiceChannel()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não esta em um canal de voz! ");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(!e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Message msg = e.sendPureMessage(EmojiList.CORRECT + " Carregando a playlist ``" + args.get(1) + "``").complete();

            if(msg == null) {
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            for(PlaylistTrackProfile pf : e.getUserProfile().getPlaylist().get(args.get(1))) {
                AudioLoaderProfile.loadAndPlay(e.getAuthor(), msg, pf.getUrl(), true, false);
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            if(e.getMessage().getMentionedUsers().size() >= 1) {

                EmbedBuilder b = new EmbedBuilder();
                b.setTitle(":musical_note: Listando todas as suas playlists");
                b.setDescription("Para adicionar músicas use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist add [nome] [musica]``");

                for(String k : Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getPlaylist().keySet()) {
                    b.addField(k, "Contem ``" + Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getPlaylist().get(k).size() + "`` musicas.", false);
                }

                b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
                b.setTimestamp(e.getMessage().getCreationTime());
                b.setColor(Color.GREEN);

                e.sendMessage(b);

                return new CommandResult(CommandResultEnum.SUCCESS);
            }


            if(e.getUserProfile().getPlaylist().size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui playlists.");
            }else{
                EmbedBuilder b = new EmbedBuilder();
                b.setTitle(":musical_note: Listando todas as suas playlists");
                b.setDescription("Para adicionar músicas use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist add [nome] [musica]``");

                for(String k : e.getUserProfile().getPlaylist().keySet()) {
                    b.addField(k, "Contem ``" + e.getUserProfile().getPlaylist().get(k).size() + "`` musicas.", false);
                }

                b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
                b.setTimestamp(e.getMessage().getCreationTime());
                b.setColor(Color.GREEN);

                e.sendMessage(b);
            }


            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("info")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "list", "playlist");
            }

            if(!e.getUserProfile().getPlaylist().containsKey(args.get(1))) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui uma playlist com este nome!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            ArrayList<PlaylistTrackProfile> tracks = e.getUserProfile().getPlaylist().get(args.get(1));

            if(tracks.size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, não há musicas na playlist ``" + args.get(1) + "``");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Paginator ph = new Paginator(e, ":musical_note: Listando as músicas da playlist " + args.get(1) + "!");

            int pages = (tracks.size() + (10 + 1)) / 10;


            Integer actual = 0;
            Integer pactual = 1;


            for (int i = 1; i <= pages; i++) {
                String pg = "";
                for (int p = actual; p < pactual * 10; p++) {
                    if (tracks.size() <= p) {
                        break;
                    }
                    actual++;
                    pg = pg + "ID " + p + " | Nome: " +  tracks.get(p).getName() + "(" + tracks.get(p).getDuration() + ")\n";
                }
                pactual++;
                ph.addPage(pg);
            }

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }
}
