package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.Lang;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.osu.OppaiManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.LogProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.ImageUtils;
import net.heyzeer0.aladdin.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AdminCommand implements CommandExecutor {

    private static LogProfile logger = new LogProfile("Admin");

    @Command(command = "admin", description = "Comandos sobre o bot", type = CommandType.BOT_ADMIN, isAllowedToDefault = false,
            usage = "", sendTyping = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lang) {
        if(args.get(0).equalsIgnoreCase("key")) {
            for(User u : e.getMessage().getMentionedUsers()) {
                Main.getDatabase().getUserProfile(u).addKeys(Integer.valueOf(args.get(1)));
            }

            e.sendMessage(EmojiList.CORRECT + " Success.");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equals("testOppai")) {

            long ms = System.currentTimeMillis();
            try{
                OppaiManager.getMapInfo(Integer.valueOf(args.get(1)));
                e.sendMessage(EmojiList.CORRECT + " Success. ``" + (System.currentTimeMillis() - ms) + "ms``");
            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Failed. " + Utils.sendToHastebin(Utils.getStackTrace(ex)));
            }


            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equals("reloadLang")) {

            long ms = System.currentTimeMillis();
            for(Lang l : Lang.values()) {
                try{
                    l.getLangProfile().refreshLang();
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            e.sendMessage(EmojiList.CORRECT + " Took ``" + (System.currentTimeMillis() - ms) + "ms`` to reload all langs.");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("updateavatar")) {
            try{
                BufferedImage inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "update_avatar.png")));
                BufferedImage author = ImageUtils.getImageFromUrl(e.getAuthor().getEffectiveAvatarUrl());

                Graphics g = inputImage.createGraphics();
                g.drawImage(inputImage,0,0,null);
                g.drawImage(author,11,8, null);
                g.drawImage(author,290,8, null);

                g.dispose();
                e.sendImageWithEmbed(inputImage, new EmbedBuilder().setColor(Color.GREEN));

            }catch (Exception ex) {ex.printStackTrace();}

            return new CommandResult(CommandResultEnum.SUCCESS);
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
