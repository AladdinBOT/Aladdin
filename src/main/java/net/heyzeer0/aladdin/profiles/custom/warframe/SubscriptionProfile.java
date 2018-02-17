package net.heyzeer0.aladdin.profiles.custom.warframe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 16/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
@Setter
public class SubscriptionProfile {

    HashMap<String, Boolean> values = new HashMap<>();

    public SubscriptionProfile() {
        this(new HashMap<>());
    }

    @ConstructorProperties({"values"})
    public SubscriptionProfile(HashMap<String, Boolean> values) {
        this.values = values;

        if(!values.containsKey("nightAlerts"))
            values.put("nightAlerts", true);
        if(!values.containsKey("rareAlerts"))
            values.put("rareAlerts", true);
        if(!values.containsKey("baroAlerts"))
            values.put("baroAlerts", true);
        if(!values.containsKey("invasionAlerts"))
            values.put("invasionAlerts", true);
        if(!values.containsKey("darvoAlerts"))
            values.put("darvoAlerts", true);
    }

    @JsonIgnore
    public boolean getValue(String key) {
        return values.getOrDefault(key, true);
    }

}
