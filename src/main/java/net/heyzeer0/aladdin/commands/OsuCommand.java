package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.OsuManager;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuBeatmapProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuPlayerProfile;
import net.heyzeer0.aladdin.utils.ImageUtils;
import net.heyzeer0.aladdin.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuCommand implements CommandExecutor {

    public static Font italic;
    public static Font bold;
    public static Font regular;

    static {
        try {
            italic = Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "fonts" + File.separator + "Exo2-Italic.otf"));
            bold = Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "fonts" + File.separator + "Exo2-Bold.ttf"));
            regular = Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "fonts" + File.separator + "Exo2-Regular.ttf"));
        }catch (Exception ex) { ex.printStackTrace(); }
    }


    @Command(command = "osu", description = "Informações sobre Osu!.", parameters = {"profile", "nick"}, type = CommandType.FUN,
            usage = "a!osu profile HeyZeer0")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("profile")) {
            Utils.runAsync(() -> {
                try{
                    OsuPlayerProfile pf = OsuManager.getUserProfile(args.get(1));


                    OsuMatchProfile mp = null;
                    OsuBeatmapProfile bp = null;
                    try{
                        mp = OsuManager.getTop10FromPlayer(pf.getNome()).get(0);
                        bp = OsuManager.getBeatmap(mp.getBeatmap_id());
                    }catch (Exception ex) { ex.printStackTrace(); }


                    if(!pf.isExist()) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o jogador informado não existe.");
                        return;
                    }

                    BufferedImage background = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "osu_profile.png")));
                    BufferedImage user_image = ImageUtils.getImageFromUrl("https://a.ppy.sh/" + pf.getUserid());
                    BufferedImage flag = ImageUtils.getImageFromUrl("https://osu.ppy.sh/images/flags/" + pf.getCountry().toUpperCase() + ".png");

                    if(user_image == null || flag == null) {
                        return;
                    }

                    if(user_image.getHeight() != 128 || user_image.getWidth() != 128) {
                        user_image = ImageUtils.resize(user_image, 128, 128);
                    }

                    Graphics2D g = background.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.drawImage(user_image, 52, 27, null);
                    g.drawImage(ImageUtils.resize(flag, 45, 30), 269, 109, null);
                    g.setFont(italic.deriveFont(45.79f));
                    g.drawString(pf.getNome(), 198, 80);
                    ImageUtils.drawCenteredString(g, "" + Math.round(Float.valueOf(pf.getLevel())), new Rectangle(205, 106, 42, 24), bold.deriveFont(23.5f));
                    ImageUtils.drawCenteredString(g, pf.getCount_rank_sh(), new Rectangle(491, 147, 56, 15), bold.deriveFont(21.38f));
                    ImageUtils.drawCenteredString(g, pf.getCount_rank_s(), new Rectangle(580, 147, 53, 15), bold.deriveFont(21.38f));
                    ImageUtils.drawCenteredString(g, pf.getCount_rank_a(), new Rectangle(666, 147, 51, 15), bold.deriveFont(21.38f));
                    ImageUtils.drawCenteredString(g, pf.getCount_rank_ssh(), new Rectangle(536, 90, 56, 15), bold.deriveFont(21.38f));
                    ImageUtils.drawCenteredString(g, pf.getCount_rank_ss(), new Rectangle(622, 90, 56, 15), bold.deriveFont(21.38f));
                    g.setFont(regular.deriveFont(18.21f));
                    g.drawString("#" + pf.getPp_rank() + " | #" + pf.getCountry_rank() + " - " + Math.round(Float.valueOf(pf.getPp_raw())) + "pp", 116, 237);
                    String beatmap = (mp == null ? "O jogador não possui uma top play." : bp.getTitle() + " [" + bp.getVersion() + "] " + " - " + Math.round(Float.valueOf(mp.getPp())) + "pp");
                    g.drawString(beatmap, 145, 215);

                    g.dispose();
                    e.sendImagePure(background, EmojiList.CORRECT + " Aqui esta o perfil de ``" + pf.getNome() + "``");

                }catch(Exception ex) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, ocorreu um erro ao tentar adquirir os dados do jogador informado: ``" + ex.getMessage() + "``");
                }
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
