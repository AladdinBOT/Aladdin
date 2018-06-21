package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.database.entities.profiles.StarboardProfile;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class StarboardCommand implements CommandExecutor {

    @Command(command = "starboard", description = "Crie ou delete starboards", aliasses = {"sboard"}, parameters = {"create/config/remove/list"}, type = CommandType.MISCELLANEOUS, isAllowedToDefault = false,
            usage = "a!starboard create 3 #starboard\na!starboard config 0 info\na!starboard config 0 setamount 3\na!starboard config 0 ignorechannel #testes\na!starboard remove 0\na!starboard list")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("create")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "criar", "quantidade necessária de emotes", "#canal");
            }

            if(e.getMessage().getMentionedChannels().size() < 1) {
                e.sendMessage(EmojiList.WORRIED + " Oops, você não mencionou nenhum canal!");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            try{

                Integer amount = Integer.valueOf(args.get(1));
                String ch = e.getMessage().getMentionedChannels().get(0).getId();

                new Reactioner(EmojiList.THINKING + " Adicione como reação nesta mensagem o emote que quer utilizar", e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                    String emote = v.getReactionEmote().getName() + "|" + (v.getReactionEmote().getId() == null ? "null" : v.getReactionEmote().getId());
                    if(e.getGuildProfile().getGuild_starboards().containsKey(emote)) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o emote mencionado já pertence a outra starboard.");
                        return;
                    }
                    e.getGuildProfile().createStarboard(emote, amount, ch);

                    e.sendMessage(EmojiList.CORRECT + " Você criou com sucesso a starboard.");
                });

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a quantidade inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("config")) {
            if(args.getSize() < 3) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "setamount/ignorechannel/info");
            }

            try{
                Integer id = Integer.valueOf(args.get(1));

                if(id < 0) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o número precisa ser maior ou igual a zero");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(e.getGuildProfile().getGuild_starboards().size() < id) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, a starboard com a id inserida não existe");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("info")) {
                    StarboardProfile pf = e.getGuildProfile().getStarboardById(id);

                    Paginator ph = new Paginator(e, ":tools: Configurações da starboard " + id);
                    ph.addPage("Emoji: " + pf.getEmote().split("\\|")[0] + "\nQuantidade necessária: " + pf.getAmount() + "\nMensages: " + pf.getMessages().size());

                    String x = "Canais Ingorados:\n";

                    if(pf.getBlocked_channels().size() >= 1) {
                        for(String k : pf.getBlocked_channels().keySet()) {
                            x = x + "#" + pf.getBlocked_channels().get(k) + " (" + k + ")\n";
                        }

                        ph.addPage(x);
                    }else{
                        ph.addPage("Não há canais a serem ignorados para adicionar utilize \n" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "starboard config " + id + " ignorechannel #canal");
                    }

                    ph.start();
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("setamount")) {
                    if(args.getSize() < 4) {
                        return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "setamount", "quantidade");
                    }

                    try{

                        Integer amount = Integer.valueOf(args.get(3));

                        if(amount <= 0) {
                            e.sendMessage(EmojiList.WORRIED + " Oops, o valor inserido é invalido");
                            return new CommandResult(CommandResultEnum.SUCCESS);
                        }

                        e.getGuildProfile().changeStarboardAmount(id, amount);
                        e.sendMessage(EmojiList.CORRECT + " Você alterou a quantidade necessária de emojis da starboard ``" + id + "`` para ``" + amount + "``");
                    }catch (Exception ex) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, o valor inserido é invalido");
                    }

                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(args.get(2).equalsIgnoreCase("ignorechannel")) {
                    if(args.getSize() < 4) {
                        return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "ignorechannel", "#canal");
                    }

                    if(e.getMessage().getMentionedChannels().size() <= 0) {
                        e.sendMessage(EmojiList.WORRIED + " Oops, você esqueceu de mencionar um canal.");
                        return new CommandResult(CommandResultEnum.SUCCESS);
                    }

                    if(e.getGuildProfile().isBlockedChannel(id, e.getMessage().getMentionedChannels().get(0))) {
                        e.getGuildProfile().removeBlockedChannelToStarboard(e.getMessage().getMentionedChannels().get(0), id);

                        e.sendMessage(EmojiList.CORRECT + " Você deixou de ignorar com sucesso o canal " + e.getMessage().getMentionedChannels().get(0).getAsMention() + " da starboard ``" + id + "``");
                    }else{
                        e.getGuildProfile().addBlockedChannelToStarboard(e.getMessage().getMentionedChannels().get(0), id);
                        e.sendMessage(EmojiList.CORRECT + " Você ignorou com sucesso o canal " + e.getMessage().getMentionedChannels().get(0).getAsMention() + " da starboard ``" + id + "``");
                    }

                    return new CommandResult(CommandResultEnum.SUCCESS);
                }


                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "config", "id", "amount/ignorechannel");
            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a id inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("remove")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "deletar", "id");
            }

            try{
                Integer id = Integer.valueOf(args.get(1));

                if(id < 0) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, o número precisa ser maior ou igual a zero");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                if(e.getGuildProfile().getGuild_starboards().size() < id) {
                    e.sendMessage(EmojiList.WORRIED + " Oops, a starboard com a id inserida não existe");
                    return new CommandResult(CommandResultEnum.SUCCESS);
                }

                e.getGuildProfile().deleteStarboard(id);

                e.sendMessage(EmojiList.CORRECT + " Você deletou com sucesso a starboard de id ``" + id + "``");

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, a id inserida é invalida");
            }

            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        if(args.get(0).equalsIgnoreCase("list")) {

            HashMap<String, StarboardProfile> starboards = e.getGuildProfile().getGuild_starboards();
            String[] keyset = starboards.keySet().toArray(new String[] {});

            if(starboards.size() <= 0) {
                e.sendMessage(EmojiList.WORRIED + " Oops, parece que você não posssui uma starboard você pode criar uma utilizando o comando ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "starboard criar``");
                return new CommandResult(CommandResultEnum.SUCCESS);
            }

            Paginator ph = new Paginator(e, ":newspaper: Listando todas as starboads!");

            int pages = (starboards.size() + (10 + 1)) / 10;

            Integer actual = 0;
            Integer pactual = 1;


            for (int i = 1; i <= pages; i++) {
                String pg = "";
                for (int p = actual; p < pactual * 10; p++) {
                    if (starboards.size() <= p) {
                        break;
                    }
                    actual++;

                    String emote = starboards.get(keyset[p]).getEmote().split("\\|")[0];

                    pg = pg + "ID " + p + " | Emote: " +  emote + "(" + starboards.get(keyset[p]).getMessages().size() + " mensagens)\n";
                }
                pactual++;
                ph.addPage(pg);

            }

            ph.start();
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

}
