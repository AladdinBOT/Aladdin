package net.heyzeer0.aladdin.profiles.custom.warframe;


/**
 * Created by HeyZeer0 on 06/04/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PriceProfile {

    String name;
    String price;

    public PriceProfile(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        switch (name.toLowerCase()) {
            case "systems":
                return "Sistemas";
            case "neuroptics":
                return "Neurovisor";
            case "set":
                return "Todas as partes";
            case "chassis":
                return "Chassi";
            case "blueprint":
                return "Diagrama";
            case "stock":
                return "Coronha";
            case "barrel":
                return "Cano";
            case "receiver":
                return "Receptor";
            case "cerebrum":
                return "Cérebro";
            case "carapace":
                return "Carapaça";
            case "handle":
                return "Cabo";
            case "blade":
                return "Lamina";
            case "hilt":
                return "Lamina";
            case "upper limb":
                return "Membro Superior";
            case "lower limb":
                return "Membro Inferior";
            case "string":
                return "Corda";
            case "grip":
                return "Cabo";
            case "link":
                return "Conexão";
        }
        return name;
    }

    public String getPrice() {
        if(price.equalsIgnoreCase("")) {
            return ":x: Preço não disponível";
        }
        return "<:platinum:364483112702443522>" + price + "l";
    }

}
