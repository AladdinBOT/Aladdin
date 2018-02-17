package net.heyzeer0.aladdin.manager.custom.warframe;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.profiles.custom.warframe.AlertProfile;
import net.heyzeer0.aladdin.profiles.custom.warframe.SubscriptionProfile;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 16/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SubscriptionManager {

    public static HashMap<String, SubscriptionProfile> subscriptions = null;
    public static ArrayList<String> sendedIds = new ArrayList<>();

    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public static void addSubscriptor(User u) {
        u.openPrivateChannel().queue(sc -> sc.sendMessage(":white_check_mark: Você agora recebera noticias sobre o jogo").queue(scc -> {
            subscriptions.put(u.getId(), new SubscriptionProfile());
            Main.getDatabase().getServer().updateSubscriptions(subscriptions);
        }, fl -> {}), fl -> {});
    }

    public static void removeSubscriptor(User u) {
        subscriptions.remove(u.getId());

        Main.getDatabase().getServer().updateSubscriptions(subscriptions);
    }

    public static void addSendedId(String x) {
        if(sendedIds.size() >= 1000) {
            sendedIds.remove(sendedIds.size() - 1);
        }
        sendedIds.add(x);
        Main.getDatabase().getServer().updateSendedIds(sendedIds);
    }

    public static void startUpdating() {
        timer.scheduleAtFixedRate(() -> {
            if(subscriptions == null) {
                subscriptions = Main.getDatabase().getServer().getSubscriptions();
                sendedIds = Main.getDatabase().getServer().getSendedIds();
            }else{

                boolean sendNight = false;
                boolean sendBaro = false;

                boolean sendDarvo = false;
                double atual;
                double original;
                double percent = 0;

                int amount = subscriptions.size();

                try{
                    JSONObject main = new JSONObject(Utils.readWebsite("https://api.warframestat.us/pc"));
                    JSONObject cycle = main.getJSONObject("cetusCycle");
                    JSONObject baro = main.getJSONObject("voidTrader");
                    JSONObject darvo = main.getJSONArray("dailyDeals").getJSONObject(0);

                    List<AlertProfile> alerts = AlertManager.getAlerts();
                    List<AlertProfile> selectedAlerts = new ArrayList<>();

                    for(AlertProfile alert : alerts) {
                        if(!sendedIds.contains(alert.getId()) && alert.hasLoot()) {
                            if(alert.getRewordID().getName().toLowerCase().contains("orokin") || alert.getRewordID().getName().contains("forma")) {
                                selectedAlerts.add(alert);
                                addSendedId(alert.getId());
                            }
                        }
                    }

                    if(!cycle.getString("id").toLowerCase().contains("nan") && !cycle.getBoolean("isDay") && !sendedIds.contains(cycle.getString("id"))) {
                        addSendedId(cycle.getString("id"));
                        sendNight = true;
                    }

                    if(!sendedIds.contains(darvo.getString("id"))) {
                        addSendedId(darvo.getString("id"));
                        sendDarvo = true;

                        atual = darvo.getInt("salePrice");
                        original = darvo.getInt("originalPrice");

                        percent = (atual / original) * 100;
                    }

                    if(baro.getBoolean("active") && !sendedIds.contains(baro.getString("id"))) {
                        addSendedId(baro.getString("id"));
                        sendBaro = true;
                    }

                    for(String userId : subscriptions.keySet()) {
                        User u = Main.getUserById(userId);
                        List<Guild> mutual = Main.getMutualGuilds(u);
                        SubscriptionProfile pf = subscriptions.get(userId);

                        if(mutual.size() <= 0) {
                            subscriptions.remove(userId);
                        }else{
                            Member m = mutual.get(0).getMember(u);

                            if(m.getOnlineStatus() == OnlineStatus.ONLINE) {

                                if(sendDarvo && pf.getValue("darvoAlerts")) {

                                    EmbedBuilder b = new EmbedBuilder();
                                    b.setTitle("<:level:363725048881610753> Status do jogo");
                                    b.setDescription("Um novo item esta em promoção no Darvo!");
                                    b.addField("<:lotus:363726000871309312> " + darvo.getString("item") + " | :clock1: " + darvo.getString("eta") + " restantes",
                                            "<:credits:363725076845035541> Estoque: " + (darvo.getInt("total") - darvo.getInt("sold")) + "/" + darvo.getInt("total") + "\n" +
                                            "<:platinum:364483112702443522> Preço: " + darvo.getInt("salePrice") + " (**" + Math.round(Math.round(percent)) + "%** off)",
                                            true);
                                    b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                                    b.setTimestamp(LocalDateTime.now());
                                    b.setColor(Color.CYAN);

                                    u.openPrivateChannel().queue((success) -> success.sendMessage(b.build()).queue(), (failure) -> subscriptions.remove(userId));
                                }

                                if(sendBaro && pf.getValue("baroAlerts")) {
                                    EmbedBuilder b = new EmbedBuilder();
                                    b.setTitle("<:level:363725048881610753> Status do jogo");
                                    b.setDescription("O comerciante do void voltou!\nTempo restante ``" + baro.getString("endString") + "``");
                                    b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                                    b.setTimestamp(LocalDateTime.now());
                                    b.setColor(Color.CYAN);

                                    u.openPrivateChannel().queue((success) -> success.sendMessage(b.build()).queue(), (failure) -> subscriptions.remove(userId));
                                }

                                if(sendNight && pf.getValue("nightAlerts")) {
                                    EmbedBuilder b = new EmbedBuilder();
                                    b.setTitle("<:level:363725048881610753> Status das planícies");
                                    b.setDescription("É noite nas planícies!\nTempo restante ``" + cycle.getString("timeLeft") + "``");
                                    b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                                    b.setTimestamp(LocalDateTime.now());
                                    b.setColor(Color.CYAN);

                                    u.openPrivateChannel().queue((success) -> success.sendMessage(b.build()).queue(), (failure) -> subscriptions.remove(userId));
                                }

                                if(selectedAlerts.size() > 0 && pf.getValue("rareAlerts")) {

                                    EmbedBuilder b = new EmbedBuilder();
                                    b.setColor(Color.CYAN);

                                    if(selectedAlerts.size() >= 2) {
                                        b.setTitle("<:level:363725048881610753> Alertas raros disponíveis", null);
                                    }else{
                                        b.setTitle("<:level:363725048881610753> Alerta raro disponível", null);
                                    }
                                    b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                                    b.setDescription("Listando todos os alertas raros disponíveis");
                                    b.setTimestamp(LocalDateTime.now());

                                    Integer alertas = 0;
                                    for (AlertProfile p : selectedAlerts) {
                                        alertas++;
                                        b.addField("<:lotus:363726000871309312> Alerta " + alertas + " | :clock1: " + p.getTimeLeft() + " restantes",
                                                "<:liset:363725081404375040> Localidade: " + p.getLocation() + " | " + p.getMission().getMission() + " | " + p.getMission().getFaction() + "\n" + (p.hasLoot() ?
                                                        "<:mod:363725102472495107> Recompensa: [" + p.getRewordID().getName() + "](" + p.getRewordID().getDirectURL() + ")" + "\n" : "") +
                                                        "<:credits:363725076845035541> Créditos: " + p.getCredits() + "\n" +
                                                        "<:level:363725048881610753> Nível mínimo: " + p.getMinLevel()
                                                , false);
                                    }

                                    if (alertas == 0) {
                                        b.addField("", "Não há alertas disponíveis", true);
                                    }

                                    b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                                    u.openPrivateChannel().queue((success) -> success.sendMessage(b.build()).queue(), (failure) -> subscriptions.remove(userId));
                                }
                            }
                        }
                    }
                }catch (Exception ex) { ex.printStackTrace();}

                if(amount != subscriptions.size())
                    Main.getDatabase().getServer().updateSubscriptions(subscriptions);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

}
