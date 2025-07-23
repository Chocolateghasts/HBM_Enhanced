package com.mewo.hbmenhanced.OpenComputers;

import com.google.gson.reflect.TypeToken;
import com.mewo.hbmenhanced.Util.JsonUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchTree {
    public static File folder;
    public static Map<String, ResearchTree> trees = new HashMap<>();
    protected File teamFile;
    protected String team;
    public static File worldDir;
    public static MinecraftServer mcServer;

    public static final String DEFAULT_TREE_PATH = "config/hbmenhanced/ResearchTree/tree.json";

    public Map<String, ResearchNode> nodes;

    public static void init(MinecraftServer server) {
        mcServer = server;
        ISaveHandler saveHandler = server.getEntityWorld().getSaveHandler();
        File worldDirectory = saveHandler.getWorldDirectory();
        worldDir = worldDirectory;
        File parent = new File(worldDirectory.getPath() + "/hbmenhanced/tree");
        folder = new File(parent, "Research_Trees");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("[HBM-Enhanced] Created Research_Trees folder at " + folder.getAbsolutePath());
            } else {
                System.err.println("[HBM-Enhanced] Failed to create Research_Trees folder at " + folder.getAbsolutePath());
            }
        }
        for (Map.Entry<String, ResearchTree> entry : trees.entrySet()) {
            ResearchTree tree = entry.getValue();
            if (tree.nodes.isEmpty()) {
                tree.load();
                if (tree.nodes.isEmpty()) {
                    tree.manualInit(server);
                    tree.save();
                }
            }
        }
    }

    public ResearchTree() {
        nodes = new HashMap<>();
    }

    public ResearchTree(String team) {
        this();
        teamFile = new File(folder, team + ".json");
        this.team = team;
        System.out.println(teamFile.getPath());
    }

    public void manualInit(MinecraftServer server) {
        File file = new File(DEFAULT_TREE_PATH);

        if (!file.exists()) {
            System.err.println("[HBM-Enhanced] Default tree file does not exist: " + file.getAbsolutePath());
            return;
        }

        Type type = new TypeToken<Map<String, ResearchNode>>() {}.getType();
        Map<String, ResearchNode> defaultTree = JsonUtil.read(file, type);

        if (defaultTree != null) {
            this.nodes = defaultTree;
            this.save();
            System.out.println("[HBM-Enhanced] Loaded default tree from: " + file.getAbsolutePath());
        } else {
            System.err.println("[HBM-Enhanced] Failed to load default tree from: " + file.getAbsolutePath());
        }
    }

    public static ResearchTree getOrCreate(String team) {
        return trees.computeIfAbsent(team, t -> {
            ResearchTree tree = new ResearchTree(t);
            tree.load();
            if (tree.nodes.isEmpty()) {
                tree.manualInit(mcServer);
            }
            return tree;
        });
    }

    public static ResearchTree getTree(String team) {
        return trees.get(team);
    }

    public ResearchNode getNode(String id) {
        return nodes.get(id);
    }

    public void addNode(ResearchNode node) {nodes.put(node.id, node);}

    public void removeNode(String id) {nodes.remove(id);}

    public void save() {
        JsonUtil.write(teamFile, nodes);
    }

    public void load() {
        Type listType = new TypeToken<Map<String, ResearchNode>>() {}.getType();
        Map<String, ResearchNode> loadedNodes = JsonUtil.read(teamFile, listType);
        if (loadedNodes != null) {
            this.nodes = loadedNodes;
        }
    }
}
