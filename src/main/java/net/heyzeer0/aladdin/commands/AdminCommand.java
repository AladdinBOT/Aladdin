package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.AkinatorProfile;
import net.heyzeer0.aladdin.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AdminCommand implements CommandExecutor {

    @Command(command = "admin", description = "Comandos sobre o bot", type = CommandType.BOT_ADMIN, isAllowedToDefault = false,
            usage = "", sendTyping = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("key")) {
            for(User u : e.getMessage().getMentionedUsers()) {
                Main.getDatabase().getUserProfile(u).addKeys(2);
            }
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("akinator")) {
            try {
                new AkinatorProfile(e);
            }catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if(args.get(0).equalsIgnoreCase("test")) {
            e.sendMessage("running the test");

            User u;

            if(e.getMessage().getMentionedUsers().size() >= 1) {
                u = e.getMessage().getMentionedUsers().get(0);
            }else{
                u = e.getAuthor();
            }

            try{
                BufferedImage inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "profile.png")));
                BufferedImage tempImage = new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
                BufferedImage author = ImageUtils.resize(ImageUtils.getImageFromUrl(u.getEffectiveAvatarUrl()), 204, 205);


                Graphics g = tempImage.createGraphics();
                g.drawImage(inputImage,0,0,null);
                g.drawImage(author,72,182, null);
                g.setColor(Color.BLACK);
                g.setFont(Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "Roboto-Thin.ttf")).deriveFont(35f));
                g.drawString(u.getName(), 320, 365);

                if(Main.getDatabase().getUserProfile(u).isPremiumActive()) {
                    BufferedImage badge = ImageUtils.resize(ImageIO.read(new File(Main.getDataFolder(), "images" + File.separator + "badge.png")), 300, 200);
                    g.drawImage(badge,490,inputImage.getMinY() + 10, null);
                }

                g.dispose();

                e.sendImage(tempImage);

            }catch (Exception ex) { ex.printStackTrace(); }
        }
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

}
