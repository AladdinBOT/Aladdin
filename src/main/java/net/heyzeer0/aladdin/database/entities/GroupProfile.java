package net.heyzeer0.aladdin.database.entities;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class GroupProfile {

    String id;
    ArrayList<String> permissions = new ArrayList<>();

    @Setter
    boolean isDefault;

    public GroupProfile(String nome, boolean isDefault) {
        this(nome, new ArrayList<>(), isDefault);
    }

    @ConstructorProperties({"id", "permissions", "isDefault"})
    public GroupProfile(String id, ArrayList<String> permissions, boolean isDefault) {
        this.id = id;
        this.permissions = permissions;
        this.isDefault = isDefault;
    }

    public void addPermission(String permission) {
        if(!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(String permission) {
        if(permissions.contains(permission)) {
            permissions.remove(permission);
        }
    }


}
