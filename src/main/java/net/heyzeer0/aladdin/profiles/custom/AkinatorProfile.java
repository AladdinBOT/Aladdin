package net.heyzeer0.aladdin.profiles.custom;

import com.google.gson.stream.JsonWriter;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.commands.AkinatorCommand;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.profiles.utilities.Reactioner;
import net.heyzeer0.aladdin.utils.ImageUtils;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONObject;
import org.json.JSONWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 22/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AkinatorProfile {

    private String NEW_SESSION_URL = "http://api-pt3.akinator.com/ws/new_session?partner=1";
    private String ANSWER_URL = "http://api-pt3.akinator.com/ws/answer";
    private String GET_GUESS_URL = "http://api-pt3.akinator.com/ws/list";
    private String CHOICE_URL = "http://api-pt3.akinator.com/ws/choice";
    private String EXCLUSION_URL = "http://api-pt3.akinator.com/ws/exclusion";

    MessageEvent e;
    AkinatorQuestion actual;
    AkinatorGuess guess;
    String signature;
    String session;

    Reactioner message;

    boolean lastQuestionWasGuess = false;
    int questions = 0;

    public AkinatorProfile(MessageEvent e) throws Exception {
        this.e = e;

        JSONObject session = Unirest.get(NEW_SESSION_URL).queryString("player", "AladdinBOT").asJson().getBody().getObject();
        actual = new AkinatorQuestion(session);
        if(actual.isGameOver()) {

            NEW_SESSION_URL = "http://api-pt4.akinator.com/ws/new_session?partner=1";
            ANSWER_URL = "http://api-pt4.akinator.com/ws/answer";
            GET_GUESS_URL = "http://api-pt4.akinator.com/ws/list";
            CHOICE_URL = "http://api-pt4.akinator.com/ws/choice";
            EXCLUSION_URL = "http://api-pt4.akinator.com/ws/exclusion";

            session = Unirest.get(NEW_SESSION_URL).queryString("player", "AladdinBOTv1.0").asJson().getBody().getObject();
            actual = new AkinatorQuestion(session);

            if(actual.isGameOver()) {
                e.sendMessage(EmojiList.WORRIED + " Oops, os servidores do akinator estão offline!");
                return;
            }
        }

        signature = actual.getSignature();
        this.session = actual.getSession();

        createNextQuestion();
    }

    public void createGuess() throws Exception {
        AkinatorCommand.akinators.put(e.getAuthor().getId(), System.currentTimeMillis());
        guess = new AkinatorGuess();
        lastQuestionWasGuess = true;
        e.getChannel().sendTyping().complete();

        BufferedImage inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_guess.png")));
        BufferedImage tempImage = new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        BufferedImage guessImage = ImageUtils.scale(ImageUtils.getImageFromUrl(guess.getImgPath()), 300, 300);


        Graphics g = tempImage.createGraphics();
        g.drawImage(inputImage,0,0,null);
        g.setColor(Color.BLACK);
        g.setFont(Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "Roboto-Thin.ttf")).deriveFont(25f));
        g.drawString(guess.getName(), 100, 85);

        g.drawImage(guessImage, 120, 100, null);

        g.dispose();

        Message msg = e.sendImagePure(tempImage, EmojiList.THINKING + " Escolha a resposta desejada!");

        if(msg != null) {
            msg.addReaction("1⃣").queue();
            msg.addReaction("2⃣").queue();

            message = new Reactioner(msg, e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                try {
                    selectOption(v.getReactionEmote().getName());
                }catch (Exception ex){ this.e.sendMessage(EmojiList.WORRIED + " Oops, um erro ocorreu ao continuar a utilizar o akinator"); ex.printStackTrace();}
            });
        }
    }

    public void createNextQuestion() throws Exception {
        Utils.runAsync(() -> {
            try{
                AkinatorCommand.akinators.put(e.getAuthor().getId(), System.currentTimeMillis());
                lastQuestionWasGuess = false;
                questions++;
                e.getChannel().sendTyping().complete();

                BufferedImage inputImage;

                if(actual.getProgression() <= 30 && questions > 4) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_30.png")));
                }else if(actual.getProgression() <= 40 && questions > 4) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_40.png")));
                }else if(actual.getProgression() <= 50) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_50.png")));
                }else if(actual.getProgression() <= 60) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_60.png")));
                }else if(actual.getProgression() <= 80) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_80.png")));
                }else if(actual.getProgression() <= 85) {
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_85.png")));
                }else{
                    inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "akinator_40.png")));
                }

                BufferedImage tempImage = new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_INT_ARGB);


                Graphics g = tempImage.createGraphics();
                g.drawImage(inputImage,0,0,null);
                g.setColor(Color.BLACK);

                g.setFont(Font.createFont(Font.TRUETYPE_FONT, new File(Main.getDataFolder(), "images" + File.separator + "Roboto-Thin.ttf")).deriveFont(25f));

                ArrayList<String> strings = new ArrayList<>();
                int index = 0;
                while (index < actual.getQuestion().length()) {
                    if(strings.size() > 0) {
                        strings.add("-" + actual.getQuestion().substring(index, Math.min(index + 26, actual.getQuestion().length())));
                    }else{
                        strings.add(actual.getQuestion().substring(index, Math.min(index + 26, actual.getQuestion().length())));
                    }

                    index += 26;
                }

                int atual = 145;
                for(String x : strings) {
                    g.drawString(x, 98, atual);
                    atual+=28;
                }

                g.dispose();

                Message msg = e.sendImagePure(tempImage, EmojiList.THINKING + " Escolha a resposta desejada!");

                if(msg != null) {
                    msg.addReaction("1⃣").complete();
                    msg.addReaction("2⃣").complete();
                    msg.addReaction("3⃣").complete();
                    msg.addReaction("4⃣").complete();
                    msg.addReaction("5⃣").complete();

                    message = new Reactioner(msg, e.getAuthor().getIdLong(), e.getChannel(), (v) -> {
                        try {
                            selectOption(v.getReactionEmote().getName());
                            Message msg2 = v.getTextChannel().getMessageById(v.getMessageId()).complete();
                            if(msg2 != null) { msg2.delete().queue(); }
                        }catch (Exception ex){ e.sendMessage(EmojiList.WORRIED + " Oops, um erro ocorreu ao continuar a utilizar o akinator"); ex.printStackTrace();}
                    });
                }
            }catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    public void selectOption(String emote) throws Exception {
        byte answer = 0;

        switch (emote) {
            case "1⃣":
                answer = 0;
                break;
            case "2⃣":
                answer = 1;
                break;
            case "3⃣":
                answer = 2;
                break;
            case "4⃣":
                answer = 3;
                break;
            case "5⃣":
                answer = 4;
                break;
            default:
                answer = 0;
                break;
        }

        if(lastQuestionWasGuess) {
            if(answer != 0 && answer != 1) {
               answer = 0;
            }
            if (answer == 0) {
                    Unirest.get(CHOICE_URL)
                            .queryString("session", session)
                            .queryString("signature", signature)
                            .queryString("step", actual.getStepNum())
                            .queryString("element", guess.getId())
                            .asString();
                    e.sendMessage(EmojiList.SMILE + " Ótimo ! Adivinhei certo mais uma vez.\n" + "Eu adorei jogar com você!");
                    AkinatorCommand.akinators.remove(e.getAuthor().getId());
            } else if (answer == 1) {
                    Unirest.get(EXCLUSION_URL)
                            .queryString("session", session)
                            .queryString("signature", signature)
                            .queryString("step", actual.getStepNum())
                            .queryString("forward_answer", answer)
                            .asString();

                    lastQuestionWasGuess = false;
                    createNextQuestion();
            }
            return;
        }

        JSONObject json = Unirest.get(ANSWER_URL)
                    .queryString("session", session)
                    .queryString("signature", signature)
                    .queryString("step", actual.getStepNum())
                    .queryString("answer", answer)
                    .asJson().getBody().getObject();
        actual = new AkinatorQuestion(json);

        if (actual.gameOver) {
            e.sendMessage(EmojiList.SMILE + " Droga ! Parece que dessa vez não consegui adivinhar.\n" + "Eu adorei jogar com você!");
            AkinatorCommand.akinators.remove(e.getAuthor().getId());
            return;
        }

        if (actual.getProgression() > 90) {
            createGuess();
        } else {
            createNextQuestion();
        }
    }

    @Getter
    class AkinatorQuestion {

        boolean gameOver;
        String signature = "";
        String session = "";
        String question;
        int stepNum;
        double progression;

        AkinatorQuestion(JSONObject json) {
            String completion = json.getString("completion");
            if ("OK".equalsIgnoreCase(completion)) {
                JSONObject params = json.getJSONObject("parameters");
                JSONObject info = params.has("step_information") ? params.getJSONObject("step_information") : params;
                question = info.getString("question");
                stepNum = info.getInt("step");
                progression = info.getDouble("progression");

                JSONObject identification = params.optJSONObject("identification");
                if (identification != null) {
                    signature = identification.getString("signature");
                    session = identification.getString("session");
                }
                gameOver = false;
            } else {
                gameOver = true;
            }
        }

    }

    @Getter
    class AkinatorGuess {

        final String id;
        final String name;
        final String desc;
        final int ranking;
        final String pseudo;
        final String imgPath;

        AkinatorGuess() throws UnirestException {
            JSONObject json = Unirest.get(GET_GUESS_URL)
                    .queryString("session", session)
                    .queryString("signature", signature)
                    .queryString("step", actual.getStepNum())
                    .asJson()
                    .getBody()
                    .getObject();

            JSONObject character = json.getJSONObject("parameters")
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("element");

            id = character.getString("id");
            name = character.getString("name");
            desc = character.getString("description");
            ranking = character.getInt("ranking");
            pseudo = character.getString("pseudo");
            imgPath = character.getString("absolute_picture_path");
        }

    }

}
