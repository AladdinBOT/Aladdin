package net.heyzeer0.aladdin.enums;

/**
 * Created by HeyZeer0 on 23/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum CommandType {

    FUN("Diversão", EmojiList.COOKIE), INFORMATIVE("Informativo", EmojiList.BOOKMARK), ADMNISTRATIVE("Administrativo", EmojiList.LOCK), MUSIC("Musica", EmojiList.MUSIC), MISCELLANEOUS("Diversos", EmojiList.DIAMOND), BOT_ADMIN("", null);

    String x;
    EmojiList e;

    CommandType(String x, EmojiList e) {
        this.x = x;
        this.e = e;
    }

    public EmojiList getEmoji() {
        return e;
    }

    @Override
    public String toString() {
        return x;
    }

}
