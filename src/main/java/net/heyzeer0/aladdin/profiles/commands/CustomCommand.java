package net.heyzeer0.aladdin.profiles.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 21/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class CustomCommand {

    public final static Pattern argsPatternWithRegex = Pattern.compile("(#regex_arg\\[(.+?)])");
    public final static Pattern argsPattern = Pattern.compile("(#arg)");
    public final static Pattern RANDOM_PATTERN = Pattern.compile("(#random\\[.+?,+.+?])", Pattern.CASE_INSENSITIVE);
    public final static Pattern RANDOMINT_PATTERN = Pattern.compile("(#random_int\\[[0-9]+])", Pattern.CASE_INSENSITIVE);

    String msg;
    String creator;

    public CustomCommand() {

    }

    public CustomCommand(String msg, String creator) {
        this.msg = msg;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public String getMsg() {
        return msg;
    }

    public String handleCommand(MessageEvent e, String[] args) {
        String message = this.msg;
        List<String> mArgs = new ArrayList<>(Arrays.asList(args));

        Integer argsB = -1;
        Matcher m2 = argsPattern.matcher(message);

        while(m2.find()) {
            argsB++;
            if(argsB >= args.length) {
                e.sendMessage(EmojiList.WORRIED + " Você não inseriu os argumentos necessários, quantidade: " + (msg.split("#arg").length - 1));
                return "";
            }
            if(args[argsB].equalsIgnoreCase(" ")) {
                e.sendMessage(EmojiList.WORRIED + " Você não inseriu os argumentos necessários, quantidade: " + (msg.split("#arg").length - 1));
                return "";
            }

            mArgs.remove(args[argsB]);
            message = message.replaceFirst(argsPattern.pattern(), args[argsB]);
        }

        Integer argsA = -1;
        Matcher m = argsPatternWithRegex.matcher(message);

        while(m.find()) {
            argsA++;
            if(argsA >= args.length) {
                e.sendMessage(EmojiList.WORRIED + " Você não inseriu os argumentos necessários, quantidade: " + (msg.split("#regex_arg").length - 1));
                return "";
            }
            if(mArgs.get(argsA).equalsIgnoreCase(" ")) {
                e.sendMessage(EmojiList.WORRIED + " Você não inseriu os argumentos necessários, quantidade: " + (msg.split("#regex_arg").length - 1));
                return "";
            }

            if(!mArgs.get(argsA).matches(m.group(2))) {
                e.sendMessage(EmojiList.WORRIED + " Você não inseriu os argumentos necessários, quantidade: " + (msg.split("#regex_arg").length - 1));
                return "";
            }

            message = message.replaceFirst(argsPatternWithRegex.pattern(), mArgs.get(argsA));
        }

        Matcher matcher = RANDOM_PATTERN.matcher(message);
        while (matcher.find()) {
            String group = matcher.group(0);
            String[] options = group.substring(group.indexOf("[") + 1, group.lastIndexOf("]")).split(",");
            int random = Utils.r.nextInt(options.length);
            group = Pattern.quote(group);
            message = message.replaceFirst(group,
                    options[random]);
        }

        Matcher matcher2 = RANDOMINT_PATTERN.matcher(message);
        while (matcher2.find()) {
            String group = matcher2.group(0);
            Integer valor = Integer.valueOf(group.substring(group.indexOf("[") + 1, group.lastIndexOf("]")));
            group = Pattern.quote(group);
            message = message.replaceFirst(group, String.valueOf(Utils.r.nextInt(valor)));
        }

        if(message.contains("#user")) {
            message = message.replace("#user", e.getMember().getEffectiveName());
        }
        if(message.contains("#mention")) {
            message = message.replace("#mention", e.getMember().getAsMention());
        }

        if(message.contains("-delete")) {
            message = message.replace("-delete", "");
            e.deleteMessage();
        }

        try{
            JSONObject object = new JSONObject(message);
            if(object.has("message") && object.get("message") instanceof String){
                e.sendMessage(object.getString("message"));
                return null;
            }
            if(object.has("embed") && object.get("embed") instanceof JSONObject){
                EmbedBuilder embed = new EmbedBuilder();
                JSONObject eo = object.getJSONObject("embed");
                if(eo.has("color") && eo.get("color") instanceof String) {
                    try{
                        embed.setColor((Color)Color.class.getField(eo.getString("color")).get(null));
                    }catch (Exception ignored) {
                        embed.setColor(Color.GREEN);
                    }
                }
                if(eo.has("author") && eo.get("author") instanceof JSONArray)embed.setAuthor(eo.getJSONArray("author").getString(0), eo.getJSONArray("author").getString(1), eo.getJSONArray("author").getString(2));
                if(eo.has("description") && eo.get("description") instanceof String) embed.setDescription(eo.getString("description"));
                if(eo.has("footer") && eo.get("footer") instanceof JSONArray) embed.setFooter(eo.getJSONArray("footer").getString(0), eo.getJSONArray("footer").getString(1));
                if(eo.has("image") && eo.get("image") instanceof String) embed.setImage(eo.getString("image"));
                if(eo.has("thumbnail") && eo.get("thumbnail") instanceof String) embed.setImage(eo.getString("thumbnail"));
                if(eo.has("title")){
                    if(eo.get("title") instanceof String) embed.setTitle(eo.getString("title"));
                    if(eo.get("title") instanceof JSONArray){
                        if(eo.getJSONArray("title").length() == 1){
                            embed.setTitle(eo.getJSONArray("title").getString(0));
                        }
                        if(eo.getJSONArray("title").length() == 2){
                            embed.setTitle(eo.getJSONArray("title").getString(0), eo.getJSONArray("title").getString(1));
                        }
                    }
                }
                if(eo.has("fields")) {
                    if(eo.get("fields") instanceof JSONArray) {
                        JSONArray fields = eo.getJSONArray("fields");
                        for(int i = 0; i< fields.length(); i++) {
                            JSONObject obj = fields.getJSONObject(i);
                            embed.addField(obj.getString("title"), obj.getString("value"), obj.getBoolean("inline"));
                        }
                    }
                }
                e.sendMessage(embed);
                return null;
            }
        }catch (Exception ex) {
            return message;
        }

        return message;
    }

}
