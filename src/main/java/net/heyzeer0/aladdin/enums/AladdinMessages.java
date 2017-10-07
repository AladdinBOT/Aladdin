package net.heyzeer0.aladdin.enums;


/**
 * Created by HeyZeer0 on 14/01/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum AladdinMessages {

    NO_PERMISSION(EmojiList.WORRIED + " Oops, parece que você não possui permissão para executar este comando. **(%s)**"),
    SUPPOST_COMMAND("Comando invalido, você quis dizer **%s**?"),
    WITHOUT_PARAMS(EmojiList.THINKING + " Oops, você esqueceu de especificar os parâmetros!!\n**Use:** %s %s");

    private String msg;

    AladdinMessages(String x) {
        msg = x;
    }

    public String getMessage() {
        return msg;
    }

    public String replaceMessage(Object... obj) {
        return String.format(msg, obj);
    }

}
