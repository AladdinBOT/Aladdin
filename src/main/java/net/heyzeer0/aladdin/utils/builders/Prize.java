package net.heyzeer0.aladdin.utils.builders;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

/**
 * Created by HeyZeer0 on 18/11/2017.
 * Copyright © HeyZeer0 - 2016
 */
@Getter
@Setter
public class Prize {

    String name = "Não definido";
    String dmMessage = "Não definido";

    @ConstructorProperties({"name", "dmMessage"})
    public Prize(String name, String dmMessage) { this.name = name; this.dmMessage = dmMessage;}

}
