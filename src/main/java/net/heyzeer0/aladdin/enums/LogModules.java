package net.heyzeer0.aladdin.enums;

/**
 * Created by HeyZeer0 on 27/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public enum LogModules {

    VOICE_MODULE(false, "Este módulo ira arquivar ações sobre canais de voz tais como mudança de canal."),
    ROLE_MODULE(true, "Este módulo ira arquivar ações sobre os cargos tais como a adição de um cargo a um membro."),
    MEMBER_MODULE(true, "Este módulo ira arquivar ações sobre os membros tais como a entrada de um."),
    ACTION_MODULE(true, "Este módulo ira arquivar ações como banimentos, kicks e outros."),
    MESSAGE_MODULE(true, "Este módulo ira arquivar ações sobre as mensagens tais como a edição de uma.");

    boolean active;
    String description;

    LogModules(boolean active, String description) {
        this.active = active;
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

}
