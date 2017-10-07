package net.heyzeer0.aladdin.manager.permissions;

import net.heyzeer0.aladdin.profiles.permissions.PermissionNode;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 30/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class NodeManager {

    public static HashMap<String, PermissionNode> nodes = new HashMap<>();

    public static PermissionNode getNode(String x) {
        if(!nodes.containsKey(x)) {
            nodes.put(x, new PermissionNode(x));
        }
        return nodes.get(x);
    }

    public static boolean validNode(String x) {
        if(x.contains(".")) {
            String[] pnodes = x.split("\\.");
            if(!nodes.containsKey(pnodes[0])) {
                return false;
            }
            if(pnodes[pnodes.length -1].equalsIgnoreCase("*")) {
                return nodes.containsKey(x.substring(0, x.length() - 2));
            }
            if(nodes.get(pnodes[0]).containsSubnode(x)) {
                return true;
            }
            return false;
        }
        return nodes.containsKey(x);
    }

}
