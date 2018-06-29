package net.heyzeer0.aladdin.commands;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.LangProfile;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;

import java.awt.*;
import java.util.List;

/**
 * Created by HeyZeer0 on 08/07/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WeatherCommand implements CommandExecutor {

    @Command(command = "weather", description = "command.weather.description", aliasses = {"weather"}, parameters = {"city"}, type = CommandType.INFORMATIVE,
            usage = "a!weather São Paulo")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e, LangProfile lp) {
        Utils.runAsync(() -> {
            try{
                YahooWeatherService service = new YahooWeatherService();
                List<Channel> channel = service.getForecastForLocation(args.getComplete(), DegreeUnit.CELSIUS).all();

                if(channel.size() == 0) {
                    e.sendMessage(lp.get("command.weather.error.invalidcity"));
                    return;
                }

                Channel i = channel.get(0);

                String pais = i.getDescription().replace("Yahoo! Weather for ", "").replace(" ", "").split(",")[2].toLowerCase();

                Integer maxima = i.getItem().getCondition().getTemp() > i.getItem().getForecasts().get(0).getHigh() ? i.getItem().getCondition().getTemp() : i.getItem().getForecasts().get(0).getHigh();

                EmbedBuilder b1 = new EmbedBuilder();
                b1.setDescription(lp.get("command.weather.embed.title", pais, i.getDescription().replace("Yahoo! Weather for ", "")));
                b1.setColor(Color.GREEN);
                b1.addField(lp.get("command.weather.embed.field.1"), ":straight_ruler: **" + lp.get("command.weather.embed.field.2") + "** " + i.getItem().getGeoLat() + " " + i.getItem().getGeoLong(), false);
                b1.addField(lp.get("command.weather.embed.field.3"), ":sun_with_face: **" + lp.get("command.weather.embed.field.4") + "** " + maxima + "°C", true);
                b1.addField("", ":snowflake: **" + lp.get("command.weather.embed.field.5") + "** " + i.getItem().getForecasts().get(0).getLow() + "°C", true);
                b1.addField("", ":thermometer: **" + lp.get("command.weather.embed.field.6") + "** " + i.getItem().getCondition().getTemp() + "°C", true);
                b1.addField(lp.get("command.weather.embed.field.7"), ":droplet: **" + lp.get("command.weather.embed.field.8") + "** " + i.getAtmosphere().getHumidity() + "%", true);
                b1.addField("", ":arrow_down: **" + lp.get("command.weather.embed.field.9") +"** " + i.getAtmosphere().getPressure(), true);
                b1.addField("", ":arrow_down: **" + lp.get("command.weather.embed.field.10") +"** " + i.getAtmosphere().getRising().name(), true);
                b1.addField(lp.get("command.weather.embed.field.11"), ":sunny: **" + lp.get("command.weather.embed.field.12") + "** " + getTime(i.getAstronomy().getSunrise().getHours() + ":" + i.getAstronomy().getSunrise().getMinutes()) + " " + i.getAstronomy().getSunrise().getConvention().name() + "   ", true);
                b1.addField("", ":full_moon: **" + lp.get("command.weather.embed.field.13") + "** " + getTime(i.getAstronomy().getSunset().getHours() + ":" + i.getAstronomy().getSunset().getMinutes()) + " " + i.getAstronomy().getSunset().getConvention().name(), true);
                b1.addBlankField(true);
                b1.addField(lp.get("command.weather.embed.field.14"), ":cloud_tornado: **" + lp.get("command.weather.embed.field.15") + "** " + i.getWind().getSpeed() + " km/h", true);
                b1.addField("", ":dash:  **" + lp.get("command.weather.embed.field.16") + "** " + i.getWind().getDirection(), true);
                b1.addBlankField(true);
                b1.addField(lp.get("command.weather.embed.field.17"), ":cloud_rain:  **" + lp.get("command.weather.embed.field.18") + "** " + Utils.translateTempo(i.getItem().getCondition().getText()), true);
                b1.setFooter("Tempo requisitado por " + e.getAuthor().getName(), e.getAuthor().getAvatarUrl());

                b1.setTimestamp(e.getMessage().getCreationTime());

                e.sendMessage(b1);
            }catch (Exception ex) {
                e.sendMessage(lp.get("command.weather.error.exception"));
                ex.printStackTrace();
            }
        });
        return new CommandResult(CommandResultEnum.SUCCESS);
    }

    private static String getTime(String valor) {
        String[] args = valor.toLowerCase().split(":");
        return (Integer.valueOf(args[0]) < 10 ? ("0" + args[0]) : args[0]) + ":" + (Integer.valueOf(args[1]) < 10 ? ("0" + args[1]) : args[1]);
    }

}
