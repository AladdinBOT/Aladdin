package net.heyzeer0.aladdin.profiles.custom.warframe;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 16/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
@Setter
public class SubscriptionProfile {

    boolean rareAlerts = true;
    boolean nightCycle = true;
    boolean rareInvasions = true;

    public SubscriptionProfile() {
        this(true, true, true);
    }

    @ConstructorProperties({"rareAlerts", "nightCycle", "rareInvasions"})
    public SubscriptionProfile(boolean rareAlerts, boolean nightCycle, boolean rareInvasions) {
        this.rareAlerts = rareAlerts;
        this.nightCycle = nightCycle;
        this.rareInvasions = rareInvasions;
    }

}
