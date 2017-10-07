package net.heyzeer0.aladdin.profiles.permissions;

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PermissionNode {

    String main;
    ArrayList<String> subnodes = new ArrayList<>();

    public PermissionNode(String main) {
        this.main = main;
    }

    public void addSubnode(String x) {
        subnodes.add(x);
    }

    public boolean containsSubnode(String x) {
        return subnodes.contains(x);
    }

    public ArrayList<String> getSubnodes() {
        return subnodes;
    }

}
