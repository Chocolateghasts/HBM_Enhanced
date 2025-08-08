package com.mewo.hbmenhanced.ResearchBlocks.Util;

import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientResearchSync {
    public static String team = "";
    public static final Map<String, ResearchNode> nodeCache = new HashMap<>();
    public static int version = 0;

    public static void fullPacket(String teamName, Map<String, ResearchNode> fullTree, int newVersion) {
        team = teamName;
        nodeCache.clear();
        nodeCache.putAll(fullTree);
        version = newVersion;
    }

    // Overload if you want to keep old code compatibility
    public static void fullPacket(String teamName, Map<String, ResearchNode> fullTree) {
        fullPacket(teamName, fullTree, -1);
    }

    public static void replaceNode(String id, ResearchNode node) {
        nodeCache.put(id, node);
    }

    public static void changeUnlocked(String id, boolean unlocked) {
        ResearchNode node = nodeCache.get(id);
        if (node == null) return;
        node.isUnlocked = unlocked;
    }

    public static ResearchNode getNode(String id) {
        return nodeCache.get(id);
    }

    public static Collection<ResearchNode> getAllNodes() {
        return nodeCache.values();
    }

    public static boolean isInitialized() {
        return !nodeCache.isEmpty();
    }

    public static String getTeam() {
        return team;
    }

    public static void setVersion(int newVersion) {
        version = newVersion;
    }

    public static int getVersion() {
        return version;
    }

    public static ResearchTree getTree(String team) {
        ResearchTree tree = new ResearchTree();
        tree.nodes = nodeCache;
        System.out.println("NODES CLENT SIDEL " + tree.nodes);
        return tree;
    }
}
