package net.heyzeer0.aladdin.enums;

import net.heyzeer0.aladdin.profiles.LangProfile;

/**
 * Created by HeyZeer0 on 09/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public enum Lang {

    PT_BR(new LangProfile("pt_br", false), "HeyZeer0#0190"),
    EN_US(new LangProfile("en_us", false), "Animadoria#8918");

    LangProfile lp; String author;

    Lang(LangProfile lp, String author) {
        this.lp = lp; this.author = author;
    }

    public LangProfile getLangProfile() {
        return lp;
    }

}
