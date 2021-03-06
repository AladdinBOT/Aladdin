package net.heyzeer0.aladdin.enums;

import net.heyzeer0.aladdin.profiles.LangProfile;

/**
 * Created by HeyZeer0 on 09/06/2018.
 * Copyright © HeyZeer0 - 2016
 */
public enum Lang {

    PT_BR(new LangProfile("pt_br"), "HeyZeer0#0190", ":flag_br:"),
    EN_US(new LangProfile("en_us"), "Animadoria#8918, HeyZeer0#8918", "\uD83C\uDDFA\uD83C\uDDF8"),
    PT_PT(new LangProfile("pt_pt"), "Animadoria#8918", ":flag_pt:");

    LangProfile lp; String author; String flag;

    Lang(LangProfile lp, String author, String flag) {
        this.lp = lp; this.author = author; this.flag = flag;
    }

    public LangProfile getLangProfile() {
        return lp;
    }

    public String getAuthor() {
        return author;
    }

    public String getFlag() {
        return flag;
    }

}
