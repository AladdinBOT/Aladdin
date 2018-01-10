package net.heyzeer0.aladdin.utils.builders;

import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.enums.GuildConfig;
import net.heyzeer0.aladdin.manager.custom.GiveawayManager;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 17/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
public class GiveawayBuilder {

    public static HashMap<String, GiveawayBuilder> builders = new HashMap<>();

    public static boolean checkMessages(GuildMessageReceivedEvent e) {
        if(builders.size() >= 1) {
            for(String x : builders.keySet()) {
                if(x.equalsIgnoreCase(e.getAuthor().getId())) {
                    GiveawayBuilder b = builders.get(x);
                    if(b.e.getChannel().getId().equalsIgnoreCase(e.getChannel().getId())) {
                        builders.get(x).receiveTextUpdate(new MessageEvent(e));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void checkReaction(MessageReactionAddEvent e) {
        if(builders.size() >= 1) {
            for(String x : builders.keySet()) {
                if(x.equalsIgnoreCase(e.getUser().getId())) {
                    builders.get(x).receiveClickUpdate(e);
                }
            }
        }
    }

    String name = "Não definido";
    long end_time = 0L;
    TextChannel ch;
    MessageEvent e;
    ArrayList<Prize> prizes = new ArrayList<>();

    String builder_message_id;
    ActualPhase phase = ActualPhase.MAIN_FRAME;
    Prize addPrize;
    String last_text_message_id;

    public GiveawayBuilder(MessageEvent e) {
        this.e = e;

        updateMessage(e);
    }

    private void updateMessage(MessageEvent e) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.GREEN);

        if(phase == ActualPhase.ADDING_PRIZE) {
            b.setTitle(":moneybag: Adicionando um premio");
            b.setDescription("Reaja com os valores indicados abaixo para os alterar" +
                    "\nquando estiver pronto clique no :white_check_mark:\n\n" +
                    ":one: Nome: " + addPrize.getName() + "\n" +
                    ":two: Mensagem privada: " + addPrize.getDmMessage());
        }else{
            b.setTitle(":moneybag: Criando seu sorteio");
            b.setDescription("Reaja com os valores indicados abaixo para os alterar" +
                    "\nquando estiver pronto clique no :white_check_mark:\n\n" +
                    ":one: Titulo do sorteio: " + name + "\n" +
                    ":two: Data de finalização: " + (end_time == 0 ? "Não definido" : Utils.getTime(end_time)) + "\n" +
                    ":three: Premios: " + prizes.size() + "\n" +
                    ":four: Canal onde ocorrera: " + (ch == null ? "Não definido" : "#" + ch.getName()));
        }

        b.setFooter("Iniciado por " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
        b.setTimestamp(e.getMessage().getCreationTime());


        if(last_text_message_id != null) {
            e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
            last_text_message_id = null;
        }

        if(builder_message_id != null) {
            e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
        }

        Message msg = e.getChannel().sendMessage(b.build()).complete();

        if(msg != null) {
            builder_message_id = msg.getId();

            if(phase == ActualPhase.ADDING_PRIZE) {
                msg.addReaction(Utils.getRegional("1")).queue();
                msg.addReaction(Utils.getRegional("2")).queue();
                msg.addReaction("✅").queue();
                msg.addReaction("\uD83D\uDED1").queue();
            }else {
                msg.addReaction(Utils.getRegional("1")).queue();
                msg.addReaction(Utils.getRegional("2")).queue();
                msg.addReaction(Utils.getRegional("3")).queue();
                msg.addReaction(Utils.getRegional("4")).queue();
                msg.addReaction("✅").queue();
                msg.addReaction("\uD83D\uDED1").queue();
            }

            if(!builders.containsKey(e.getAuthor().getId())) {
                builders.put(e.getAuthor().getId(), this);
            }

            this.e = e;
        }
    }

    public void receiveClickUpdate(MessageReactionAddEvent ev) {
        if(!ev.getChannel().getId().equals(e.getChannel().getId()) && !e.getAuthor().getId().equals(ev.getUser().getId())) {
            return;
        }

        if(phase == ActualPhase.ADDING_PRIZE) {

            if(ev.getReactionEmote().getName().equals("✅")) {

                if(addPrize.getName().equalsIgnoreCase("Não definido")) {
                    ev.getTextChannel().sendMessage(EmojiList.WORRIED + " Oops, você precisa ao menos definir o nome do premio").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                prizes.add(addPrize);
                addPrize = null;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("\uD83D\uDED1")) {
                addPrize = null;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("1⃣")) {
                phase = ActualPhase.WAITING_FOR_PRIZE_NAME;

                Message msg = e.getChannel().sendMessage(EmojiList.THINKING + " Digite no chat o título desejado.").complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("2⃣")) {
                phase = ActualPhase.WAITING_FOR_PRIZE_DM;

                Message msg = e.getChannel().sendMessage(EmojiList.THINKING + " Digite no chat a mensagem que deseja que seja enviado ao usuário ganhador por DM.").complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }

            return;
        }
        if(phase == ActualPhase.MAIN_FRAME) {

            if(ev.getReactionEmote().getName().equals("✅")) {

                if(name.equalsIgnoreCase("Não definido")) {
                    ev.getTextChannel().sendMessage(EmojiList.WORRIED + " Oops, você precisa definir o nome do sorteio.").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(end_time == 0) {
                    ev.getTextChannel().sendMessage(EmojiList.WORRIED + " Oops, você precisa definir o tempo do sorteio.").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(ch == null) {
                    ev.getTextChannel().sendMessage(EmojiList.WORRIED + " Oops, você precisa definir o canal do sorteio.").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }
                if(prizes.size() <= 0) {
                    ev.getTextChannel().sendMessage(EmojiList.WORRIED + " Oops, você precisa ao menos adicionar um premio.").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                if(last_text_message_id != null) {
                    e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
                    last_text_message_id = null;
                }

                if(builder_message_id != null) {
                    e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
                }

                builders.remove(e.getAuthor().getId());

                e.sendMessage(EmojiList.CORRECT + " Sorteio criado com sucesso.");
                GiveawayManager.createGiveway(this);
                return;
            }
            if(ev.getReactionEmote().getName().equals("\uD83D\uDED1")) {
                if(last_text_message_id != null) {
                    e.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
                    last_text_message_id = null;
                }

                if(builder_message_id != null) {
                    e.getChannel().getMessageById(builder_message_id).queue(scs -> scs.delete().queue());
                }

                builders.remove(e.getAuthor().getId());
                return;
            }
            if(ev.getReactionEmote().getName().equals("1⃣")) {
                phase = ActualPhase.WAITING_FOR_TITLE;

                Message msg = e.getChannel().sendMessage(EmojiList.THINKING + " Digite no chat o título do sorteio.").complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("2⃣")) {
                phase = ActualPhase.WAITING_FOR_TIME;

                Message msg = e.getChannel().sendMessage(EmojiList.THINKING + " Digite no chat quanto tempo deseja para o sorteio. Ex ``2h = 2 horas | 2m = 2 minutos``").complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }
            if(ev.getReactionEmote().getName().equals("3⃣")) {
                phase = ActualPhase.ADDING_PRIZE;
                addPrize = new Prize("Não definido", "Não definido");

                updateMessage(e);
                return;
            }
            if(ev.getReactionEmote().getName().equals("4⃣")) {
                phase = ActualPhase.WAITING_FOR_CHANNEL;

                Message msg = e.getChannel().sendMessage(EmojiList.THINKING + " Mencione no chat o canal cujo o sorteio ocorrerá").complete();

                if(msg != null) {
                    last_text_message_id = msg.getId();
                }
                return;
            }

            return;
        }
    }

    public void receiveTextUpdate(MessageEvent ev) {
        if(!ev.getChannel().getId().equals(e.getChannel().getId())) {
            return;
        }

        if(ev.getMessage().getContentDisplay().equalsIgnoreCase("cancelar")) {
            if(phase == ActualPhase.WAITING_FOR_PRIZE_NAME || phase == ActualPhase.WAITING_FOR_PRIZE_DM) {
                phase = ActualPhase.ADDING_PRIZE;
            }else{
                phase = ActualPhase.MAIN_FRAME;
            }

            ev.getChannel().getMessageById(last_text_message_id).queue(scs -> scs.delete().queue());
            return;
        }

        if(phase == ActualPhase.WAITING_FOR_CHANNEL) {
            if(ev.getMessage().getMentionedChannels().size() <= 0) {
                e.getChannel().sendMessage(EmojiList.WORRIED + " Oops, você deve mencionar um canal ou digitar ``cancelar`` para cancelar.").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                return;
            }

            ch = ev.getMessage().getMentionedChannels().get(0);
            phase = ActualPhase.MAIN_FRAME;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_PRIZE_DM) {
            addPrize.setDmMessage(ev.getMessage().getContentRaw());
            phase = ActualPhase.ADDING_PRIZE;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_PRIZE_NAME) {
            addPrize.setName(ev.getMessage().getContentRaw());
            phase = ActualPhase.ADDING_PRIZE;

            updateMessage(ev);
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_TIME) {
            boolean minute = ev.getMessage().getContentDisplay().contains("m");

            try{

                Integer value = Integer.valueOf(ev.getMessage().getContentDisplay().replace("m", "").replace("h", ""));

                if(!minute && !ev.getUserProfile().isPremiumActive()) {
                    if(value > 24) {
                        ev.getChannel().sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 24 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                        return;
                    }
                }
                if(minute && !ev.getUserProfile().isPremiumActive()) {
                    if(value > 1440) {
                        ev.getChannel().sendMessage(EmojiList.BUY + " Você não pode exceder o tempo maximo de 24 horas. Você pode ignorar esta limitação ativando uma chave premium, para mais informações utilize ``" + e.getGuildProfile().getConfigValue(GuildConfig.PREFIX) + "premium``").queue(scs -> scs.delete().queueAfter(3, TimeUnit.SECONDS));
                        return;
                    }
                }

                long time = (minute ? (60000 * value) : (3600000 * value));

                end_time = time;
                phase = ActualPhase.MAIN_FRAME;

                updateMessage(ev);

            }catch (Exception ex) {
                e.sendMessage(EmojiList.WORRIED + " Oops, o tempo inserido é invalido, você pode digitar ``cancelar`` para cancelar.");
            }
            return;
        }
        if(phase == ActualPhase.WAITING_FOR_TITLE) {
            name = ev.getMessage().getContentRaw();
            phase = ActualPhase.MAIN_FRAME;

            updateMessage(ev);
            return;
        }
    }

    enum ActualPhase {
        MAIN_FRAME, ADDING_PRIZE, WAITING_FOR_CHANNEL, WAITING_FOR_TITLE, WAITING_FOR_TIME, WAITING_FOR_PRIZE_NAME, WAITING_FOR_PRIZE_DM
    }

}
