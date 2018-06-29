package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.warframe.AlertManager;
import net.heyzeer0.aladdin.manager.custom.warframe.PriceManager;
import net.heyzeer0.aladdin.manager.custom.warframe.SubscriptionManager;
import net.heyzeer0.aladdin.manager.custom.warframe.WikiManager;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.custom.warframe.AlertProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.ArmorProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.PriceProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.WikiProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.List;

/**
 * Created by HeyZeer0 on 04/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WarframeCommand implements CommandExecutor {

    @Command(command = "warframe", description = "command.warframe.description", parameters = {"alerts/wiki/price/armor/subscription"}, sendTyping = false, type = CommandType.FUN,
            usage = "a!warframe alerts\na!warframe wiki plastids\na!warframe price Trinity Prime\na!warframe armor 10\na!warframe armor 10 5 10")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        if(args.get(0).equalsIgnoreCase("subscription")) {

            if(SubscriptionManager.subscriptions.containsKey(e.getAuthor().getId())) {
                SubscriptionManager.removeSubscriptor(e.getAuthor());
                e.sendMessage(lp.get("command.warframe.subscription.success.1"));
            }else{
                SubscriptionManager.addSubscriptor(e.getAuthor());
                e.sendMessage(lp.get("command.warframe.subscription.success.2"));
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("price")) {
            if (args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "warframe preço", "item");
            }

            Utils.runAsync(() -> {

                try{
                    String item = args.getCompleteAfter(1);
                    List<PriceProfile> p = PriceManager.getPrices(item);

                    if (p == null || p.size() <= 0) {
                        e.sendMessage(lp.get("command.warframe.error.notfound", item));
                        return;
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.CYAN);
                    b.setTitle(lp.get("command.warframe.price.embed.title", item), null);
                    b.setDescription(lp.get("command.warframe.price.embed.description"));

                    for (PriceProfile a : p) {
                        b.addField(a.getName(), a.getPrice(), true);
                    }

                    b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");

                    b.setFooter("Warframe Status - Powered by Nexus-Stats", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                    b.setTimestamp(e.getMessage().getCreationTime());

                    e.sendMessage(b);
                }catch (Exception ex) {
                    e.sendMessage(lp.get("command.warframe.price.error", ex.getMessage()));
                }
            });

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("wiki")) {
            if (args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "warframe wiki", "artigo");
            }

            String titulo = args.getCompleteAfter(1);

            Utils.runAsync(() -> {

                WikiProfile p = WikiManager.getWikiArticle(titulo);

                if (p == null) {
                    e.sendMessage(lp.get("command.warframe.error.notfound", titulo));
                    return;
                }

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setTitle(lp.get("command.warframe.wiki.embed.title", titulo), null);
                b.setDescription(lp.get("command.warframe.wiki.embed.description", p.getId()));
                if (p.hasThumbnail()) {
                    b.setImage(p.getThumbnail());
                }

                b.addField(lp.get("command.warframe.wiki.embed.field"), p.getDescription(), false);
                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");

                e.sendMessage(b);
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("alerts")) {

            Utils.runAsync(() -> {

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setTitle(lp.get("command.warframe.alerts.embed.title"), null);
                b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                b.setDescription(lp.get("command.warframe.alerts.embed.description"));

                Integer alertas = 0;
                for (AlertProfile p : AlertManager.getAlerts()) {
                    alertas++;
                    b.addField("<:lotus:363726000871309312> " + lp.get("command.warframe.alerts.embed.field.1", alertas) + " | :clock1: " + lp.get("command.warframe.alerts.embed.field.2", p.getTimeLeft()),
                               "<:liset:363725081404375040> " + lp.get("command.warframe.alerts.embed.field.3", p.getLocation() + " | " + p.getMission().getMission() + " | " + p.getMission().getFaction()) + "\n" + (p.hasLoot() ?
                               "<:mod:363725102472495107> " + lp.get("command.warframe.alerts.embed.field.4", p.getRewordID().getName(), p.getRewordID().getDirectURL()) + "\n" : "") +
                               "<:credits:363725076845035541> " + lp.get("command.warframe.alerts.embed.field.5", p.getCredits()) + "\n" +
                               "<:level:363725048881610753> " + lp.get("command.warframe.alerts.embed.field.6", p.getMinLevel())
                               , false);
                }

                if (alertas == 0) {
                    b.addField("", lp.get("command.warrfame.alerts.embed.field.7"), true);
                }

                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);

            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("armor")) {
            if (args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "warframe armor", lp.get("command.warframe.armor.arg"));
            }
            if (args.getSize() == 2) {
                if (!NumberUtils.isCreatable(args.get(1))) {
                    e.sendPrivateMessage(lp.get("command.warframe.armor.error.invalidnumber"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                ArmorProfile.ArmorInfo values = new ArmorProfile(Integer.valueOf(args.get(1))).simpleCalc();
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setDescription(lp.get("command.warframe.armor.embed.description"));
                b.setTitle(lp.get("command.warframe.armor.embed.1.title"), null);
                b.addField(":crossed_swords: " + lp.get("command.warframe.armor.embed.1.field.1"), "" + values.getPercent() + "%", false);
                b.addField(":star: " + lp.get("command.warframe.armor.embed.1.field.2"), values.getProject().toString(), false);
                b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            if (args.getSize() == 4) {
                if (!NumberUtils.isCreatable(args.get(1)) || !NumberUtils.isCreatable(args.get(2)) || !NumberUtils.isCreatable(args.get(3))) {
                    e.sendPrivateMessage(lp.get("command.warframe.armor.error.invalidnumber"));
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                ArmorProfile.ArmorInfo values = new ArmorProfile(Integer.valueOf(args.get(1)), Integer.valueOf(args.get(2)), Integer.valueOf(args.get(3))).advancedCalc();
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setDescription(lp.get("command.warframe.armor.embed.description"));
                b.setTitle(lp.get("command.warframe.armor.embed.2.title"), null);
                b.addField(":shield: " + lp.get("command.warframe.armor.embed.2.field.1"), "" + values.getArmourAmount(), false);
                b.addField(":crossed_swords: " + lp.get("command.warframe.armor.embed.2.field.2"), "" + values.getPercent() + "%", false);
                b.addField(":star: " + lp.get("command.warframe.armor.embed.2.field.3"), values.getProject().toString(), false);
                b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
