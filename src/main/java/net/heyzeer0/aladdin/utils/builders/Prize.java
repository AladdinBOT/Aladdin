package net.heyzeer0.aladdin.utils.builders;


import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 18/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class Prize {

    String name = "Não definido";
    String dmMessage = "Não definido";

    @ConstructorProperties({"name", "dmMessage"})
    public Prize(String name, String dmMessage) { this.name = name; this.dmMessage = dmMessage;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDmMessage() {
        return dmMessage;
    }

    public void setDmMessage(String dmMessage) {
        this.dmMessage = dmMessage;
    }

}
