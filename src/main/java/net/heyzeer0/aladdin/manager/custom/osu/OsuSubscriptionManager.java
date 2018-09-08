package net.heyzeer0.aladdin.manager.custom.osu;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.commands.OsuCommand;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.OsuMods;
import net.heyzeer0.aladdin.manager.utilities.ThreadManager;
import net.heyzeer0.aladdin.profiles.custom.osu.OppaiInfo;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuBeatmapProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuMatchProfile;
import net.heyzeer0.aladdin.profiles.custom.osu.OsuPlayerProfile;
import net.heyzeer0.aladdin.profiles.utilities.ScheduledExecutor;
import net.heyzeer0.aladdin.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 21/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OsuSubscriptionManager {

    private static HashMap<String, ArrayList<String>> subscription;
    private static ArrayList<String> sended_ids = new ArrayList<>();

    private static HashMap<String, Float> last_pp = new HashMap<>();

    public static final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    public static boolean addSubscriptor(User user, String target) {
        if(subscription.containsKey(target) && subscription.get(target).contains(user.getId())) {
            subscription.get(target).remove(user.getId());
            Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
            return false;
        }

        user.openPrivateChannel().queue(pc -> {
            pc.sendMessage(":white_check_mark: By now you are going to receive news about ``" + target + "``").queue(c -> {
                if(subscription.containsKey(target)) {
                    subscription.get(target).add(user.getId());
                    Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                }else{
                    try{
                        OsuManager.getTop50FromPlayer(target).forEach(k -> sended_ids.add(k.toString()));
                        ArrayList<String> ss = new ArrayList<>(); ss.add(user.getId());
                        subscription.put(target, ss);
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }catch (Exception ex) { }
                }
                });
        });

        return true;
    }

    public static void startUpdating() {
        ThreadManager.registerScheduledExecutor(new ScheduledExecutor(60000, () -> {
            if(subscription == null) {
                subscription = Main.getDatabase().getServer().getOsu_subscriptions();

                if(subscription.size() > 0) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    for(String user : subscription.keySet()) {
                        try{
                            for(OsuMatchProfile c : OsuManager.getTop50FromPlayer(user)) {
                                sended_ids.add(c.toString());
                            }
                        }catch (Exception ex) { ex.printStackTrace(); toRemove.add(user); }
                    }

                    if(toRemove.size() > 0) {
                        toRemove.forEach(c -> subscription.remove(c));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                }
            }else {
                ArrayList<String> toRemove = new ArrayList<>();
                HashMap<String, ArrayList<String>> removeUsers = new HashMap<>();
                if (subscription.size() > 0) {
                    for (String user : subscription.keySet()) {
                        try {
                            ArrayList<OsuMatchProfile> ls = OsuManager.getTop50FromPlayer(user);
                            OsuPlayerProfile pp = OsuManager.getUserProfile(user, false);

                            for (int i = 0; i < ls.size(); i++) {
                                OsuMatchProfile mp = ls.get(i);
                                if (!sended_ids.contains(mp.toString())) {
                                    sended_ids.add(mp.toString());

                                    if(pp == null) {
                                        toRemove.add(user);
                                        continue;
                                    }
                                    OsuBeatmapProfile bp = OsuManager.getBeatmap(mp.getBeatmap_id());

                                    String mods = "";
                                    for(OsuMods m : mp.getMods()) {
                                        mods = mods + m.getShortName();
                                    }

                                    OppaiInfo oi = OppaiManager.getMapInfo(mp.getBeatmap_id(), mods);

                                    double percentage = calculatePercentage(Integer.valueOf(mp.getCount50()), Integer.valueOf(mp.getCount100()), Integer.valueOf(mp.getCount300()), Integer.valueOf(mp.getCountmiss()));

                                    BufferedImage area = new BufferedImage(663, 251, 2);
                                    BufferedImage cover = ImageUtils.resize(ImageUtils.getImageFromUrl("https://assets.ppy.sh/beatmaps/" + bp.getBeatmapset_id() + "/covers/cover.jpg"), 655, 182);
                                    BufferedImage overlay = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "osu_play_profile.png")));

                                    Kernel kernel = new Kernel(3, 3, new float[] { 1f / 15f, 1f / 15f, 1f / 15f,
                                            1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f });
                                    BufferedImageOp op = new ConvolveOp(kernel);
                                    cover = op.filter(cover, null);


                                    Graphics2D g2d = area.createGraphics();
                                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                                    g2d.drawImage(cover, 5, 4, null);
                                    g2d.drawImage(overlay, 0, 0, null);

                                    BufferedImage user_image = ImageUtils.getImageFromUrl("https://a.ppy.sh/" + pp.getUserid());
                                    if(user_image != null) {
                                        if(user_image.getHeight() != 128 || user_image.getWidth() != 128) {
                                            user_image = ImageUtils.resize(user_image, 128, 128);
                                        }
                                        g2d.drawImage(user_image, 52, 31, null);
                                    }

                                    g2d.setFont(OsuCommand.italic.deriveFont(45.79f));
                                    g2d.drawString(pp.getNome(), 196, 148);
                                    g2d.setFont(OsuCommand.italic.deriveFont(25.9f));
                                    g2d.drawString(shortString(bp.getTitle(), 25) + " [" + bp.getVersion() + "]", 190, 59);
                                    g2d.setFont(OsuCommand.italic.deriveFont(19.13f));
                                    g2d.drawString(shortString(bp.getArtist(), 30), 208, 76);
                                    g2d.setFont(OsuCommand.regular.deriveFont(18.21f));
                                    g2d.drawString(
                                            Math.round(Float.valueOf(mp.getPp()))
                                            + "pp (" + Math.round(oi.getPp()) + "pp) ", 90, 210);
                                    g2d.drawString(decimalFormat.format(percentage * 100) + "% - " + mp.getMaxcombo() + "x - " + mp.getCount50() + "x 50 | " + mp.getCount100() + "x 100 | " + mp.getCountmiss() + "x miss - " + mp.getRank().replace("H", "+"), 122, 236);

                                    if(mp.getMods().size() > 0) {

                                        int x = 212;
                                        for(OsuMods mod : mp.getMods()) {
                                            BufferedImage modImg = ImageUtils.resize(mod.getImage(), 36, 25);
                                            if(modImg == null) continue;

                                            g2d.drawImage(modImg, x, 85, null);
                                            x+=40;
                                        }

                                    }

                                    g2d.dispose();

                                    final int i2 = i;
                                    for (String usr : subscription.get(user)) {
                                        User u = Main.getUserById(usr);

                                        u.openPrivateChannel().queue(c -> sendImagePure(c, area, EmojiList.CORRECT + " New rank #" + (i2 + 1) + " for " + pp.getNome() + "(+" + (Float.valueOf(pp.getPp_raw()) - last_pp.get(pp.getUserid())) + "pp)").queue(v -> {
                                        }, k -> {
                                            if (removeUsers.containsKey(user)) {
                                                removeUsers.get(user).add(usr);
                                            } else {
                                                ArrayList<String> rmv = new ArrayList<>();
                                                rmv.add(usr);
                                                removeUsers.put(user, rmv);
                                            }
                                        }), er -> {
                                            if (removeUsers.containsKey(user)) {
                                                removeUsers.get(user).add(usr);
                                            } else {
                                                ArrayList<String> rmv = new ArrayList<>();
                                                rmv.add(usr);
                                                removeUsers.put(user, rmv);
                                            }
                                        });
                                    }

                                }
                            }

                            last_pp.put(pp.userid, Float.valueOf(pp.getPp_raw()));
                        } catch (Exception ex) {
                            Main.getLogger().exception(ex);
                        }
                    }

                    if (toRemove.size() > 0) {
                        toRemove.forEach(c -> subscription.remove(c));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                    if (removeUsers.size() > 0) {
                        removeUsers.keySet().forEach(c -> subscription.get(c).removeAll(removeUsers.get(c)));
                        Main.getDatabase().getServer().updateOsuSubscriptions(subscription);
                    }
                }
            }
        }));
    }

    private static RestAction<Message> sendImagePure(PrivateChannel ch, BufferedImage img, String msg) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (Exception ex) { ex.printStackTrace();}
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        return ch.sendFile(is, "ata.png", new MessageBuilder().append(msg).build());
    }

    public static double calculatePercentage(int count50, int count100, int count300, int misses) {
        return (double)((count50 * 50) + (count100 * 100) + (count300 * 300)) / ((misses + count50 + count100 + count300) * 300);
    }

    public static String shortString(String x, int max) {
        if(x.length() <= max) {
            return x;
        }

        return x.substring(0, x.length() - (x.length() - max));
    }

}
