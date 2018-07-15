package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.osu.OppaiManager;
import net.heyzeer0.aladdin.manager.custom.osu.OsuManager;
import net.heyzeer0.aladdin.manager.custom.osu.OsuSubscriptionManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuBeatmapProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuPlayerProfile;
import net.heyzeer0.aladdin.utils.ImageUtils;
import net.heyzeer0.aladdin.utils.Utils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
            bold = Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "fonts" + File.separator + "Exo2-Bold.otf"));
            regular = Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "fonts" + File.separator + "Exo2-Regular.otf"));
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    private static Random r = new Random();

    @Command(command = "osu", description = "command.osu.description", parameters = {"profile/follow/recent/setuser/recommend"}, type = CommandType.FUN,
            usage = "a!osu profile HeyZeer0\na!osu profile\na!osu follow HeyZeer0\na!osu recent\na!osu recent HeyZeer0\na!osu setuser HeyZeer0\na!osu recommend\na!osu recommend [pp]\na!osu recommend [pp] [mods]\na!osu recommend [mods]\na!osu recommend nomod")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("recommend") || args.get(0).equalsIgnoreCase("rec")) {
            final String nick;

            if(!e.getUserProfile().getOsuUsername().equalsIgnoreCase("")) {
                nick = e.getUserProfile().getOsuUsername();
            }else{
                e.sendPureMessage(lp.get("command.osu.nonickset", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "osu setuser [nick]")).queueAfter(500, TimeUnit.MILLISECONDS);
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Utils.runAsync(() -> {
                try{
                    String mods = "";
                    int pp = 0;

                    if(args.getSize() >= 2) {
                        if(NumberUtils.isCreatable(args.get(1))) {
                            pp = Integer.valueOf(args.get(1));
                        }else{
                            mods = args.get(1).toUpperCase();
                        }
                    }
                    if(args.getSize() >= 3) {
                        mods = args.get(2).toUpperCase();
                    }

                    ArrayList<String> ignored = new ArrayList<>();

                    if(pp == 0) {
                        ArrayList<OsuMatchProfile> top10 = OsuManager.getTop10FromPlayer(nick);
                        int totalpp = 0;
                        for (OsuMatchProfile mm : top10) {
                            ignored.add(Utils.toMD5(mm.getBeatmap_id() + OsuMods.asString(mm.getMods())));
                            totalpp+= Math.round(Float.valueOf(mm.getPp()));
                        }

                        pp = totalpp / 10;
                        pp += r.nextInt(35);
                    }

                    ArrayList<OsuMatchProfile> recent = OsuManager.getRecentFromPlayer(nick, 50);
                    recent.forEach(c -> ignored.add(Utils.toMD5(c.getBeatmap_id() + OsuMods.asString(c.getMods()))));

                    OsuPlayerProfile player = OsuManager.getUserProfile(nick, false);

                    OppaiInfo map100;
                    if(mods.equalsIgnoreCase("")) {
                        map100 = Main.getDatabase().getMapByPPRange(pp, ignored);
                    }else if(mods.equalsIgnoreCase("nomod")) {
                        map100 = Main.getDatabase().getMapByPPRange(pp, ignored, "");
                    }else{
                        map100 = Main.getDatabase().getMapByPPRange(pp, ignored, mods);
                    }

                    if(map100 == null) {
                        e.sendMessage(lp.get("command.osu.recommend.error"));
                        return;
                    }

                    //dab

                    OppaiInfo map99 = OppaiManager.getMapByAcurracy(map100.getBeatmap_id(), map100.getMods_str(), 99);
                    OppaiInfo map98 = OppaiManager.getMapByAcurracy(map100.getBeatmap_id(), map100.getMods_str(), 98);
                    OppaiInfo mapPlayer = OppaiManager.getMapByAcurracy(map100.getBeatmap_id(), map100.getMods_str(), Double.valueOf(player.accuracy));

                    OsuBeatmapProfile bp = OsuManager.getBeatmap(map100.getBeatmap_id());

                    BufferedImage area = new BufferedImage(663, 251, 2);
                    BufferedImage cover = ImageUtils.resize(ImageUtils.getImageFromUrl("https://assets.ppy.sh/beatmaps/" + bp.getBeatmapset_id() + "/covers/cover.jpg"), 655, 182);
                    BufferedImage overlay = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "osu_play_profile.png")));

                    Kernel kernel = new Kernel(3, 3, new float[]{1f / 15f, 1f / 15f, 1f / 15f,
                            1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f});
                    BufferedImageOp op = new ConvolveOp(kernel);
                    cover = op.filter(cover, null);

                    Graphics2D g2d = area.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.drawImage(cover, 5, 4, null);
                    g2d.drawImage(overlay, 0, 0, null);

                    BufferedImage user_image = ImageUtils.getImageFromUrl("https://a.ppy.sh/" + player.getUserid());
                    if (user_image != null) {
                        if (user_image.getHeight() != 128 || user_image.getWidth() != 128) {
                            user_image = ImageUtils.resize(user_image, 128, 128);
                        }
                        g2d.drawImage(user_image, 52, 31, null);
                    }

                    g2d.setFont(italic.deriveFont(45.79f));
                    g2d.drawString(player.getNome(), 196, 148);
                    g2d.setFont(italic.deriveFont(25.9f));
                    ImageUtils.drawStringWithSizeLimit(g2d, bp.getTitle() + " [" + bp.getVersion() + "]", 190, 59, 453);
                    g2d.setFont(italic.deriveFont(19.13f));
                    g2d.drawString(OsuSubscriptionManager.shortString(bp.getArtist(), 30), 208, 76);
                    g2d.setFont(regular.deriveFont(18.21f));
                    g2d.drawString("Your % = " + Math.round(mapPlayer.getPp()) + "pp | 98% = " + Math.round(map98.getPp()) + "pp | 99% = " + Math.round(map99.getPp()) + "pp | 100% = " + Math.round(map100.getPp()) + "pp", 90, 210);
                    g2d.drawString("Stars: " + Math.round(map100.getStars()) + " - " + map100.getMax_combo() + "x | AR: " + Math.round(map100.getAr()) + " OD: " + Math.round(map100.getOd()) + " HP: " + Math.round(map100.getHp()) + " CS: " + Math.round(map100.getCs()) + " BPM: " + Math.round(Double.valueOf(bp.getBpm())) + " " + (map100.getMods_str().equals("") ? "" : "+" + map100.getMods_str()), 122, 236);

                    g2d.dispose();

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.GREEN);
                    b.setDescription("[Normal](https://osu.ppy.sh/d/" + bp.getBeatmapset_id() + ") - [No Vid](https://osu.ppy.sh/d/" + bp.getBeatmapset_id() + "n) - [Bloodcat](https://bloodcat.com/osu/s/" + bp.getBeatmapset_id() + ")");

                    e.sendImageWithEmbed(area, b);
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.osu.recommend.error"));
                    Main.getLogger().exception(ex);
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("setuser")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "setuser", "nick");
            }

            String nick = args.getCompleteAfter(1);

            Utils.runAsync(() -> {
                try{
                    OsuPlayerProfile pp = OsuManager.getUserProfile(nick, false);
                    e.getUserProfile().updateOsuUsername(pp.getNome());

                    e.sendMessage(lp.get("command.osu.setuser.success", pp.getNome()));
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.osu.invalidplayer"));
                }
            });

            e.getUserProfile().updateOsuUsername(args.getCompleteAfter(1));

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equals("recent") || args.get(0).equalsIgnoreCase("r")) {
            final String nick;

            if(args.getSize() >= 2 && e.getMessage().getMentionedUsers().size() <= 0) {
                nick = args.getCompleteAfter(1);
            }else if(e.getMessage().getMentionedUsers().size() > 0 && !Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getOsuUsername().equals("")) {
                nick = Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getOsuUsername();
            }else if(!e.getUserProfile().getOsuUsername().equalsIgnoreCase("")) {
                nick = e.getUserProfile().getOsuUsername();
            }else{
                e.sendPureMessage(lp.get("command.osu.nonickset", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "osu setuser [nick]")).queueAfter(500, TimeUnit.MILLISECONDS);
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "recent", "nick");
            }


            Utils.runAsync(() -> {

                try {
                    ArrayList<OsuMatchProfile> matches = OsuManager.getRecentFromPlayer(nick, 10);

                    if(matches.size() <= 0) {
                        e.sendMessage(lp.get("command.osu.recent.error.nomatches", nick));
                        return;
                    }

                    OsuMatchProfile mp = matches.get(0);

                    OsuPlayerProfile pp = OsuManager.getUserProfile(mp.getUser_id(), true);
                    OsuBeatmapProfile bp = OsuManager.getBeatmap(mp.getBeatmap_id());

                    String mods = "";
                    for (OsuMods m : mp.getMods()) {
                        mods = mods + m.getShortName();
                    }

                    OppaiInfo oi = OppaiManager.getMapInfo(mp.getBeatmap_id(), mods);
                    OppaiInfo full = OppaiManager.getMapInfo(mp.getBeatmap_id(), mp);

                    double percentage = OsuSubscriptionManager.calculatePercentage(Integer.valueOf(mp.getCount50()), Integer.valueOf(mp.getCount100()), Integer.valueOf(mp.getCount300()), Integer.valueOf(mp.getCountmiss()));
                    double mapCompletion = (double)((
                            Integer.valueOf(mp.getCount50())
                                    + Integer.valueOf(mp.getCount100())
                                    + Integer.valueOf(mp.getCount300())
                                    + Integer.valueOf(mp.getCountmiss()))
                            *100)/
                            (oi.getNum_sliders()
                                    + oi.getNum_circles()
                                    + oi.getNum_spinners());

                    BufferedImage area = new BufferedImage(663, 251, 2);
                    BufferedImage cover = ImageUtils.resize(ImageUtils.getImageFromUrl("https://assets.ppy.sh/beatmaps/" + bp.getBeatmapset_id() + "/covers/cover.jpg"), 655, 182);
                    BufferedImage overlay = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "osu_play_profile.png")));

                    Kernel kernel = new Kernel(3, 3, new float[]{1f / 15f, 1f / 15f, 1f / 15f,
                            1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f});
                    BufferedImageOp op = new ConvolveOp(kernel);
                    cover = op.filter(cover, null);


                    Graphics2D g2d = area.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.drawImage(cover, 5, 4, null);
                    g2d.drawImage(overlay, 0, 0, null);

                    BufferedImage user_image = ImageUtils.getImageFromUrl("https://a.ppy.sh/" + pp.getUserid());
                    if (user_image != null) {
                        if (user_image.getHeight() != 128 || user_image.getWidth() != 128) {
                            user_image = ImageUtils.resize(user_image, 128, 128);
                        }
                        g2d.drawImage(user_image, 52, 31, null);
                    }

                    g2d.setFont(italic.deriveFont(45.79f));
                    g2d.drawString(pp.getNome(), 196, 148);
                    g2d.setFont(italic.deriveFont(25.9f));
                    ImageUtils.drawStringWithSizeLimit(g2d, bp.getTitle() + " [" + bp.getVersion() + "]", 190, 59, 453);
                    g2d.setFont(italic.deriveFont(19.13f));
                    g2d.drawString(OsuSubscriptionManager.shortString(bp.getArtist(), 30), 208, 76);
                    g2d.setFont(regular.deriveFont(18.21f));
                    g2d.drawString(Math.round(full.getPp()) + "pp (" + Math.round(oi.getPp()) + "pp)" + (mods.equals("") ? "" : " +" + mods) + " [Map Completion " + OsuSubscriptionManager.decimalFormat.format(mapCompletion) + "%]", 90, 210);
                    g2d.drawString(OsuSubscriptionManager.decimalFormat.format(percentage * 100) + "% - " + mp.getMaxcombo() + "x - " + mp.getCount50() + "x 50 | " + mp.getCount100() + "x 100 | " + mp.getCountmiss() + "x miss - " + mp.getRank().replace("H", "+"), 122, 236);

                    g2d.dispose();

                    e.sendImagePure(area, lp.get("command.osu.recent.success", nick));
                }catch (Exception ex) {
                    Main.getLogger().exception(ex);
                    e.sendMessage(lp.get("command.osu.invalidplayer"));
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equals("follow") || args.get(0).equalsIgnoreCase("f")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "recent", "nick");
            }

            Utils.runAsync(() -> {
                try{
                    OsuPlayerProfile p = OsuManager.getUserProfile(args.getCompleteAfter(1), false);

                    if(OsuSubscriptionManager.addSubscriptor(e.getAuthor(), p.getNome())) {
                        e.sendMessage(lp.get("command.osu.follow.success.1"));
                    }else{
                        e.sendMessage(String.format(lp.get("command.osu.follow.success.2"), args.getCompleteAfter(1)));
                    }

                }catch (Exception x) { e.sendMessage(lp.get("command.osu.invalidplayer"));}
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("profile") || args.get(0).equalsIgnoreCase("p")) {
            final String nick;

            if(args.getSize() >= 2 && e.getMessage().getMentionedUsers().size() <= 0) {
                nick = args.getCompleteAfter(1);
            }else if(e.getMessage().getMentionedUsers().size() > 0 && !Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getOsuUsername().equals("")) {
                nick = Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).getOsuUsername();
            }else if(!e.getUserProfile().getOsuUsername().equalsIgnoreCase("")) {
                nick = e.getUserProfile().getOsuUsername();
            }else{
                e.sendPureMessage(lp.get("command.osu.nonickset", e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "osu setuser [nick]")).queueAfter(500, TimeUnit.MILLISECONDS);
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "recent", "nick");
            }

            Utils.runAsync(() -> {
                try{
                    OsuPlayerProfile pf = OsuManager.getUserProfile(nick, false);


                    OsuMatchProfile mp = null;
                    OsuBeatmapProfile bp = null;
                    try{
                        mp = OsuManager.getTop50FromPlayer(pf.getNome()).get(0);
                        bp = OsuManager.getBeatmap(mp.getBeatmap_id());
                    }catch (Exception ex) { ex.printStackTrace(); }


                    if(!pf.isExist()) {
                        e.sendMessage(lp.get("command.osu.invalidplayer"));
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
                    String beatmap = (mp == null ? lp.get("command.osu.profile.notopplay") : bp.getTitle() + " [" + bp.getVersion() + "] " + " - " + Math.round(Float.valueOf(mp.getPp())) + "pp");
                    ImageUtils.drawStringWithSizeLimit(g, beatmap, 145, 215, 263);
                    g.drawString(beatmap, 145, 215);

                    g.dispose();
                    e.sendImagePure(background, String.format(lp.get("command.osu.profile.success"), pf.getNome()));

                }catch(Exception ex) {
                    ex.printStackTrace();
                    e.sendMessage(String.format(lp.get("command.osu.profile.error"), "O jogador definido não existe."));
                }
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
