package net.heyzeer0.aladdin.commands;

import com.github.goive.steamapi.SteamApi;
import com.github.goive.steamapi.data.SteamApp;
import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Created by HeyZeer0 on 02/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class SteamCommand implements CommandExecutor {

    public static SteamApi steam = new SteamApi("BR");
    public static SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    @Command(command = "steam", description = "Obtenha informações da Steam.", parameters = {"game"}, type = CommandType.MISCELLANEOUS,
            usage = "a!steam game Portal 2")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("game")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "game", "nome do jogo/id");
            }

            SteamApp app;

            if(NumberUtils.isCreatable(args.get(1))) {
                try{
                    app = steam.retrieve(Integer.valueOf(args.get(1)));
                }catch (Exception ex) {
                    if(ex.getMessage().contains("Invalid")) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o jogo inserido não existe.");
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }
                    e.sendMessage(EmojiList.WORRIED + " Oops, não foi possível requerir este jogo.");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }else{
                try{
                    app = steam.retrieve(args.getCompleteAfter(1));
                }catch (Exception ex) {
                    if(ex.getMessage().contains("No appId found")) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o jogo inserido não existe.");
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }
                    e.sendMessage(EmojiList.WORRIED + " Oops, não foi possível requerir este jogo.");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }
            }

            String plataform = "";

            if(app.isAvailableForWindows())
                plataform = plataform + "<:windows:386493142582689793> ";
            if(app.isAvailableForMac())
                plataform = plataform + "<:mac:386492925963403265> ";
            if(app.isAvailableForLinux())
                plataform = plataform + "<:linux:386492926739480577> ";


            EmbedBuilder b = new EmbedBuilder();
            b.setImage(app.getHeaderImage());
            b.setTitle("<:steam:386492331794235432> " + app.getName() + " " + plataform + (app.getRequiredAge() >= 18 ? " | <:18:386500850597494794>" : ""), app.getWebsite());
            b.setColor(Color.GREEN);

            String description = app.getAboutTheGame().split("<br>")[0];
            if(description.length() > 1200) {
                description = description.substring(0, description.length() - (description.length() - 1200));
                description = description + "...";
            }

            b.setDescription(description.replaceAll("<(.|\\n)*?>", ""));

            b.addField(":moneybag: Preço", "R$" + app.getPrice() + " " + (app.isDiscounted() ? "**(" + app.getPriceDiscountPercentage() + "% off)**" : ""), true);
            b.addField(":star: Lançamento", (app.getReleaseDate() != null ? dt.format(app.getReleaseDate()) : "Não definido"), true);

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }
}
