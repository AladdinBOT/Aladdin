package net.heyzeer0.aladdin.enums;

/**
 * Created by HeyZeer0 on 23/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum CommandType {

    FUN(EmojiList.COOKIE), INFORMATIVE(EmojiList.BOOKMARK), ADMNISTRATIVE(EmojiList.LOCK), MUSIC(EmojiList.MUSIC), MISCELLANEOUS(EmojiList.DIAMOND), BOT_ADMIN(null);

    EmojiList e;

    CommandType(EmojiList e) {
        this.e = e;
    }

    public EmojiList getEmoji() {
        return e;
    }

}
