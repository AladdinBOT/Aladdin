package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.manager.custom.warframe.AlertManager;
import net.heyzeer0.aladdin.manager.custom.warframe.PriceManager;
import net.heyzeer0.aladdin.manager.custom.warframe.SubscriptionManager;
import net.heyzeer0.aladdin.manager.custom.warframe.WikiManager;
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

    @Command(command = "warframe", description = "Informações sobre warframe", parameters = {"alertas/wiki/preço/armadura/subscription"}, sendTyping = false, type = CommandType.FUN,
            usage = "a!warframe alertas\na!warframe wiki plastids\na!warframe preço Trinity Prime\na!warframe armadura 10\na!warframe armadura 10 5 10")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("subscription")) {

            if(SubscriptionManager.subscriptions.containsKey(e.getAuthor().getId())) {
                SubscriptionManager.removeSubscriptor(e.getAuthor());
                e.sendMessage(EmojiList.CORRECT + " Você agora não receberá mais notificações do jogo");
            }else{
                SubscriptionManager.addSubscriptor(e.getAuthor());
                e.sendMessage(EmojiList.THINKING + " Tentei te registrar no programa, se você recebeu uma mensagem no privado significa que foi um sucesso, caso contrario cheque se eu posso te enviar mensagens privadas! ^O^");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("preço") || args.get(0).equalsIgnoreCase("preco")) {
            if (args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "warframe preço", "item");
            }

            Utils.runAsync(() -> {

                try{
                    String item = args.getCompleteAfter(1);
                    List<PriceProfile> p = PriceManager.getPrices(item);

                    if (p == null || p.size() <= 0) {
                        e.sendMessage("Desculpe te decepcionar operador, não encontrei nada sobre " + item + ".");
                        return;
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.CYAN);
                    b.setTitle("Preços dos diagramas de " + item, null);
                    b.setDescription("Preços exibidos apenas em [**Platinas**](http://pt-br.warframe.wikia.com/wiki/Platina)");

                    for (PriceProfile a : p) {
                        b.addField(a.getName(), a.getPrice(), true);
                    }

                    b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");

                    b.setFooter("Warframe Status - Powered by Nexus-Stats", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                    b.setTimestamp(e.getMessage().getCreationTime());

                    e.sendMessage(b);
                }catch (Exception ex) {
                    e.sendMessage("Aguarde um pouco enquanto analizo os dados. Erro ``" + ex.getMessage() + "``! O operador gostou deste deboche?");
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
                    e.sendMessage("Desculpe te decepcionar operador, não encontrei nada sobre " + titulo + ".");
                    return;
                }

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setTitle("Pesquisa na Wiki - " + titulo, null);
                b.setDescription("ID do material pesquisado: " + p.getId());
                if (p.hasThumbnail()) {
                    b.setImage(p.getThumbnail());
                }

                b.addField("Descrição:", p.getDescription(), false);
                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");

                e.sendMessage(b);
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("alertas")) {

            Utils.runAsync(() -> {

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setTitle("Alertas disponíveis", null);
                b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                b.setDescription("Listando todos os alertas disponíveis");

                Integer alertas = 0;
                for (AlertProfile p : AlertManager.getAlerts()) {
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
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);

            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if (args.get(0).equalsIgnoreCase("armadura") || args.get(0).equalsIgnoreCase("armor")) {
            if (args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "warframe armadura", "[quantidade] ou [armadura base] [nível base] [nível atual]");
            }
            if (args.getSize() == 2) {
                if (!NumberUtils.isCreatable(args.get(1))) {
                    e.sendPrivateMessage(EmojiList.WORRIED + " Oops, o número indicado é invalido.");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                ArmorProfile.ArmorInfo values = new ArmorProfile(Integer.valueOf(args.get(1))).simpleCalc();
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setDescription("``Ordis processou os seus dados operador.``");
                b.setTitle("Calculo de armadura simples", null);
                b.addField(":crossed_swords: Redução de Dano:", "" + values.getPercent() + "%", false);
                b.addField(":star: Pontos de corrosão:", values.getProject().toString(), false);
                b.setThumbnail("http://vignette4.wikia.nocookie.net/warframe/images/c/ce/OrdisArchwingtrailer.png/revision/latest?cb=20140823050147");
                b.setFooter("Warframe Status", "http://img05.deviantart.net/b8d4/i/2014/327/a/8/warframe_new_logo_look__vector__by_tasquick-d87fzxg.png");
                b.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b);
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            if (args.getSize() == 4) {
                if (!NumberUtils.isCreatable(args.get(1)) || !NumberUtils.isCreatable(args.get(2)) || !NumberUtils.isCreatable(args.get(3))) {
                    e.sendPrivateMessage(EmojiList.WORRIED + " Oops, o número indicado é invalido.");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                ArmorProfile.ArmorInfo values = new ArmorProfile(Integer.valueOf(args.get(1)), Integer.valueOf(args.get(2)), Integer.valueOf(args.get(3))).advancedCalc();
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.CYAN);
                b.setDescription("``Ordis processou os seus dados operador.``");
                b.setTitle("Calculo de armadura avançado", null);
                b.addField(":shield: Armadura total:", "" + values.getArmourAmount(), false);
                b.addField(":crossed_swords: Redução de Dano:", "" + values.getPercent() + "%", false);
                b.addField(":star: Pontos de corrosão:", values.getProject().toString(), false);
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
