package net.heyzeer0.aladdin.commands;

import net.heyzeer0.aladdin.configs.instances.ApiKeysConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.enums.EmojiList;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import net.heyzeer0.aladdin.utils.Utils;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.champion_mastery.dto.ChampionMastery;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.spectator.dto.CurrentGameParticipant;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 17/04/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LeagueOfLegendsCommand implements CommandExecutor {

    public static RiotApi apiClient = new RiotApi(new ApiConfig().setKey(ApiKeysConfig.league_of_legends_key));
    public static ChampionList champions = null;
    public static HashMap<Integer, Champion> championsById = new HashMap<>();

    @Command(command = "leagueoflegends", description = "Comandos Relacionados a League of Legends", aliasses = {"lol"}, parameters = {"activeGame"}, type = CommandType.FUN,
            usage = "a!lol activeGame Zer0Master")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(champions == null) {
            try{
                champions = apiClient.getDataChampionList(Platform.BR);

                for(Champion c : champions.getData().values()) {
                    championsById.put(c.getId(), c);
                }
            }catch (Exception ex) { ex.printStackTrace(); }
        }

        if(args.get(0).equalsIgnoreCase("activeGame")) {
            if(args.getSize() < 2) {
                return new CommandResult(CommandResultEnum.MISSING_ARGUMENT, "activeGame", "jogador");
            }

            Utils.runAsync(() -> {
                String player = args.getCompleteAfter(1);
                try{
                    Summoner user = apiClient.getSummonerByName(Platform.BR, player);

                    CurrentGameInfo info = apiClient.getActiveGameBySummoner(Platform.BR, user.getId());

                    String message = "```autohotkey\n\uD83C\uDFAE Status da Partida (Summoners Rift - " + info.getGameMode() + ")";

                    HashMap<Integer, ArrayList<CurrentGameParticipant>> teams = new HashMap<>();

                    for(CurrentGameParticipant participant : info.getParticipants()) {
                        ArrayList<CurrentGameParticipant> users = new ArrayList<>();
                        if(!teams.containsKey(participant.getTeamId())) {
                            users.add(participant);
                            teams.put(participant.getTeamId(), users);
                            continue;
                        }
                        users = teams.get(participant.getTeamId());
                        users.add(participant);
                        teams.put(participant.getTeamId(), users);
                    }

                    for(Integer team : teams.keySet()) {
                        message = message + "\n\n================";
                        int id = 0;
                        for(CurrentGameParticipant participant : teams.get(team)) {
                            id++;
                            message = message + handleParticipant(participant, id);
                        }
                    }

                    message = message + "\n```";

                    e.sendMessage(message);
                }catch (Exception ex) { e.sendMessage(EmojiList.WORRIED + " Oops, o jogador inserido é invalido ou não esta em uma partida!"); }
            });
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.NOT_FOUND);
    }

    public static String handleParticipant(CurrentGameParticipant participant, int ids) {
        String message = "";

        String id = ids == 10 ? "10" : "0" + ids;


        boolean onFire = false;
        String elo = "Unranked";
        try{
            LeaguePosition[] pp = apiClient.getLeaguePositionsBySummonerId(Platform.BR, participant.getSummonerId()).toArray(new LeaguePosition[] {});
            LeaguePosition league = null;

            for(LeaguePosition pp2 : pp) {
                if(pp2.getQueueType().equalsIgnoreCase("RANKED_SOLO_5x5")) {
                    league = pp2;
                    break;
                }
            }

            if (league != null) {
                elo = league.getTier() + " " + league.getRank() + " - " + league.getWins() + "W " + league.getLosses() + "L " + league.getLeaguePoints()  + "pdl";

                onFire = league.isHotStreak();
            }


            ChampionMastery mastery = null;
            try{
                mastery = apiClient.getChampionMasteriesBySummonerByChampion(Platform.BR, participant.getSummonerId(), participant.getChampionId());
            }catch (Exception ex) { }


            message = message + "\n\n";
            message = message + " [" + id + "]  > " + championsById.get(participant.getChampionId()).getName()  + (onFire ? " \uD83D\uDD25" : "");
            message = message + "\n          Maestria: " + (mastery != null ? mastery.getChampionLevel() : "0");
            message = message + "\n          Elo: " + elo;
            message = message + "\n          Invocador: " + participant.getSummonerName();
        }catch (Exception ex) { }

        return message;
    }


}
