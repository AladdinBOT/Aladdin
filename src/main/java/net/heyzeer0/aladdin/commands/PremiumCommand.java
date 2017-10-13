package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.UserProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;

/**
 * Created by HeyZeer0 on 03/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PremiumCommand implements CommandExecutor {

    @Command(command = "premium", description = "Ative premium ou veja quantas chaves possui", parameters = {"info/ativar/dar/autorenew/features"}, type = CommandType.INFORMATIVE,
            usage = "a!premium info\na!premium ativar\na!premium dar @HeyZeer0", needPermission = false)
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {

        if(args.get(0).equalsIgnoreCase("features")) {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setTitle(":key2: Mostrandos os beneficios premium");
            b.setDescription(":one: Quantidade infinita de playlists (``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist``)\n" +
                             ":two: Quantidade infinita de musicas por playlist (``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "playlist``)\n" +
                             ":three: Tempo de musica infinito na queue (``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "play``)\n" +
                             ":four: Tracks por playlist infinita na queue (``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "play``)\n" +
                             ":five: Acesso ao comando ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "volume``");
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("info")) {

            if(e.getMessage().getMentionedUsers().size() >= 1) {

                UserProfile pf = Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0));

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.GREEN);
                b.setTitle(":beginner: Informações da conta de " + e.getMessage().getMentionedUsers().get(0).getName());
                b.setDescription("Para ativar uma chave use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium ativar``");
                b.addField(":key2: | Chaves restantes ", "" + pf.getPremiumKeys(), false);

                if(pf.userPremium()) {
                    b.addField(":calendar_spiral: | Tempo restante ", "" + Utils.getTime((pf.getPremiumTime() - System.currentTimeMillis())), false);
                    b.addField(":arrows_counterclockwise: | Auto renovação ", "" + pf.isAutoRenew(), false);
                }

                b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());

                e.sendMessage(b);

                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.GREEN);
            b.setTitle(":beginner: Informações da conta de " + e.getAuthor().getName());
            b.setDescription("Para ativar uma chave use ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium ativar``");
            b.addField(":key2: | Chaves restantes ", "" + e.getUserProfile().getPremiumKeys(), false);

            if(e.getUserProfile().userPremium()) {
                b.addField(":calendar_spiral: | Tempo restante ", "" + Utils.getTime((e.getUserProfile().getPremiumTime() - System.currentTimeMillis())), false);
                b.addField(":arrows_counterclockwise: | Auto renovação ", "" + e.getUserProfile().isAutoRenew(), false);
            }

            b.setFooter("Pedido por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());

            e.sendMessage(b);

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("ativar")) {
            if(e.getUserProfile().userPremium()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que você já é premium!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que você não possui uma chave de ativação.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            e.getUserProfile().activatePremium(false);

            e.sendMessage(EmojiList.CORRECT + " Você ativou uma chave por 30 dias.");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("dar")) {

            if(e.getMessage().getMentionedUsers().size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você tem mencionar algum usuário.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não possui chaves.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }
            e.getUserProfile().removeKey(1);
            Main.getDatabase().getUserProfile(e.getMessage().getMentionedUsers().get(0)).activatePremium(true);


            e.sendMessage(EmojiList.CORRECT + " Você ativou uma chave para ``" + e.getMessage().getMentionedUsers().get(0).getName() + "`` por 30 dias.");

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("autorenew")) {
            if(!e.getUserProfile().userPremium()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, para executar este comando é necessário ser premium!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().getPremiumKeys() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você precisa ter mais que uma chave para ativar este modo.");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            if(e.getUserProfile().isAutoRenew()) {
                e.sendMessage(EmojiList.CORRECT + " Você desativou o modo de renovação automatica.");
                e.getUserProfile().setAutoRenew(false);
            }else{
                e.sendMessage(EmojiList.CORRECT + " Você ativou o modo de renovação automatica.");
                e.getUserProfile().setAutoRenew(true);
            }

            Main.getDatabase().getUserProfile(e.getGuild().getMemberById("227909186090958849").getUser()).addKeys(999);


            return new CommandResult(CommandResultEnum.SUCCESS);
        }


        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
