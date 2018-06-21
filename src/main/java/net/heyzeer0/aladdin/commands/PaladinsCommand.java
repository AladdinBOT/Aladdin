package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;
import net.heyzeer0.papi.PaladinsAPI;
import net.heyzeer0.papi.enums.Platform;
import net.heyzeer0.papi.exceptions.SessionException;
import net.heyzeer0.papi.exceptions.UnknowPlayerException;
import net.heyzeer0.papi.profiles.requests.PaladinsChampion;
import net.heyzeer0.papi.profiles.requests.PaladinsPlayer;
import net.heyzeer0.papi.profiles.requests.PlayerStatus;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by HeyZeer0 on 19/07/17.
 * Copyright © HeyZeer0 - 2016 ~ 2017
 */
public class PaladinsCommand implements CommandExecutor {

    public static PaladinsAPI api = new PaladinsAPI(ApiKeysConfig.paladins_dev_id, ApiKeysConfig.paladins_dev_key, Platform.PC);
    public static DecimalFormat df = new DecimalFormat("###.#");

    @Command(command = "paladins", description = "Informações sobre Paladins", parameters = {"profile"}, type = CommandType.FUN,
            usage = "a!paladins perfil HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("profile")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "profile", "nick");
            }

            Utils.runAsync(() -> {
                try{
                    PaladinsPlayer player = api.getRequestManager().requestPlayer(args.get(1));
                    List<PaladinsChampion> champs = api.getRequestManager().requestUserChampions(args.get(1));
                    PaladinsChampion champ = champs.get(0);
                    PlayerStatus status = api.getRequestManager().requestPlayerStatus(args.get(1));

                    Integer kills = 0;
                    Integer deaths = 0;

                    for(PaladinsChampion ch : champs) {
                        kills+=ch.getKills();
                        deaths+=ch.getDeaths();
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.GREEN);
                    b.setThumbnail(champ.getIcon().getUrl());
                    b.setAuthor("Perfil de " + player.getName() + " | Status " + status.getStatus().toString().replace("_", " "), null, "https://web2.hirez.com/paladins//wp-content/uploads/2016/06/cropped-2016-06-03-192x192.png");
                    b.setFooter("Powered by Paladins Java API", "http://www.eclypsia.com/content/Paladins/HRX/LogoPaladinsPetit.png");
                    b.setDescription("Usuário nível ``" + player.getLevel() + "`` | Região ``" + player.getRegion() + "``");
                    b.addField(":clipboard: Perfil:", "**Vitórias:** " + player.getWins() + "\n"
                                        + "**Derrotas:** " + player.getLosses() + "\n"
                                        + "**Abandonos:** " + player.getLeaves(), true);
                    b.addField(":trophy: Campeão:", "**Nome:** " + champ.getName() + "\n"
                            + "**Vitórias:** " + champ.getWins() + "\n"
                            + "**Derrotas:** " + champ.getLosses(), true);
                    b.addField(":crossed_swords: Perfil:", "**Kills:** " + kills+ "\n"
                            + "**Deaths:** " + deaths + "\n"
                            + "**KDR:** " + df.format((double)kills / (double)deaths), true);
                    b.addField(":crossed_swords: Campeão:", "**Kills:** " + champ.getKills() + "\n"
                                                          + "**Deaths:** " + champ.getDeaths() + "\n"
                                                          + "**KDR:** " + df.format((double)champ.getKills() / (double)champ.getDeaths()), true);
                    e.sendMessage(b);


                }catch (UnknowPlayerException ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, parece que não ha um jogador com este nome!");
                }catch (SessionException ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, ocorreu um erro ao criar a seção: " + ex.getLocalizedMessage());
                }catch (Exception ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, ocorreu um erro ao realizar este comando.");
                    ex.printStackTrace();
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
