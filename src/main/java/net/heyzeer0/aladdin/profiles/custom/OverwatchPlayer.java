package net.heyzeer0.aladdin.profiles.custom;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
@Setter
public class OverwatchPlayer {

    String username;
    Integer level;
    String portrait;
    Integer quickplayWins;
    String quickplayTime;
    Integer competitiveWins;
    Integer competitiveLosts;
    Integer competitiveMatchs;
    Integer competitiveDraw;
    String competitiveTime;
    String competitiveRank;
    String competitiveImg;
    String levelFrame;
    String star;

    public OverwatchPlayer() { }

}
