package net.heyzeer0.aladdin.profiles.custom.warframe;

/**
 * Created by HeyZeer0 on 04/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MissionProfile {

    private final String mission;
    private final String faction;

    public MissionProfile(String mission, String faction) {
        this.mission = mission;
        this.faction = faction;
    }

    public String getMission() {
        switch (mission.toLowerCase()) {
            case "spy":
                return "Espionagem";
            case "mobile defense":
                return "Defesa Móvel";
            case "extermination":
                return "Extermínio";
            case "rescue":
                return "Resgate";
            case "defense":
                return "Defesa";
            case "survival":
                return "Sobrevivência";
            default:
                return mission;
        }
    }

    public String getFaction() {
        if(faction.equalsIgnoreCase("Corrupted") || faction.equalsIgnoreCase("Infestation")) {
            return "Infestados";
        }
        return faction;
    }

    @Override
    public String toString() {
        return mission;
    }

}
