package net.heyzeer0.aladdin.enums;

/**
 * Created by HeyZeer0 on 06/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum GuildConfig {

    PREFIX("a!", "Prefixo principal para comandos"),
    MEMBER_CREATE_CMDS(true, "Define se membros irão poder criar comandos."),
    MEMBER_CAN_REPEAT(true, "Permite que membros alterer o modo de repetição do player de musica"),
    MINECRAFT_CRASHHELPER(false, "Permite que membros envie um crash report no canal de suporte para auto-analise"),
    THE_GAME(true, "Ativa \"o jogo\" na guilda");

    Object df;
    String ds;

    GuildConfig(Object x, String description) {
        df = x;
        ds = description;
    }

    public Object getDefault() {
        return df;
    }

    public String getDescription() {
        return ds;
    }

}
