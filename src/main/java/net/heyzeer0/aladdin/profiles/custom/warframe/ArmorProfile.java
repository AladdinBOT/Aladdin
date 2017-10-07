package net.heyzeer0.aladdin.profiles.custom.warframe;

import java.text.DecimalFormat;

/**
 * Created by HeyZeer0 on 14/05/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ArmorProfile {

    DecimalFormat decimalFormat = new DecimalFormat("0.##");

    Integer baseArmor;
    Integer baseLevel;
    Integer currentLevel;

    public ArmorProfile(Integer armor) {
        this.baseArmor = armor;
    }

    public ArmorProfile(Integer baseArmor, Integer baseLevel, Integer currentLevel) {
        this.baseArmor = baseArmor;
        this.baseLevel = baseLevel;
        this.currentLevel = currentLevel;
    }

    public ArmorInfo simpleCalc() {
        double armor = baseArmor;
        return new ArmorInfo((armor / (300 + armor)) * 100, Math.ceil((8 * Math.log10(armor))));
    }

    public ArmorInfo advancedCalc() {
        double armor = baseArmor * (1 + ((Math.pow(currentLevel - baseLevel, 1.75)) / 200));


        return new ArmorInfo((armor / (armor + 300)) * 100, Math.ceil(8 * Math.log10(armor)), armor);
    }

    public class ArmorInfo {

        double percent;
        Integer project;
        double armourAmount;

        public ArmorInfo(double percent, double project) {
            this.percent = percent;
            this.project = (int)project;
        }

        public ArmorInfo(double percent, double project, double armorAmount) {
            this.percent = percent;
            this.project = (int)project;
            this.armourAmount = armorAmount;
        }

        public String getPercent() {
            return decimalFormat.format(percent);
        }

        public Integer getProject() {
            return project;
        }

        public Integer getArmourAmount() {
            return (int)armourAmount;
        }

    }

}
