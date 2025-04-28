package com.mewo.hbmenhanced.OpenComputers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mewo.hbmenhanced.getRpValue;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Type;
import java.util.*;
import java.io.*;

public class ResearchTree {
    public MinecraftServer server;// = getRpValue.getServer();
    public File worldDir;// = server.getEntityWorld().getSaveHandler().getWorldDirectory();
    public File treeSaveFile;// = new File(worldDir + "/hbmenhanced/tree/nodes/", "Nodes.json");
    private List<ResearchNode> nodeList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();

    public ResearchTree(MinecraftServer server) {
        this.server = server;
        this.worldDir = server.getEntityWorld().getSaveHandler().getWorldDirectory();
        this.treeSaveFile = new File(worldDir, "hbmenhanced/tree/nodes/Nodes.json");

        getNodes(); // optional preload
    }



    public ResearchNode getNode(String id) {
        for (ResearchNode node : getNodes()) {
            if (node.id.equalsIgnoreCase(id)) {
                return node;
            }
        }
        return new ResearchNode();
    }

    public List<ResearchNode> getNodes() {
        try {
            treeSaveFile.getParentFile().mkdirs();
            treeSaveFile.createNewFile();
            Gson gson = new Gson();

            FileReader reader = new FileReader(treeSaveFile);
            Type listType = new TypeToken<ArrayList<ResearchNode>>() {}.getType();
            nodeList = gson.fromJson(reader, listType);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded nodes: " + nodeList);
        return nodeList;
    }

    public void saveNodes(List<ResearchNode> nodes) {
        try (FileWriter writer = new FileWriter(treeSaveFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            gson.toJson(nodes, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNodes(int id, int amount) {
        List<ResearchNode> nodes = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            String nodeId = "node_" + (id + i);

            if (!idList.contains(nodeId)) {
                ResearchNode newNode = new ResearchNode();
                newNode.id = nodeId;
                newNode.name = "Node " + (id + i);
                newNode.description = "Auto-generated node";
                newNode.level = 1;

                nodes.add(newNode);
            }
        }

        saveNodes(nodes);
    }

    public void editNode(
            String nodeId,
            String name,
            String description,
            Integer level,
            Boolean unlocked,
            String category,
            Integer templateId,
            List<String> dependencies,
            Map<getRpValue.researchType, Integer> requirements,
            Map<String, Boolean> teamUnlocked  // Add this parameter
    ) {
        getNodes();

        for (ResearchNode node : nodeList) {
            if (node.id.equalsIgnoreCase(nodeId)) {
                if (name != null) node.name = name;
                if (description != null) node.description = description;
                if (level != null) node.level = level;
                if (unlocked != null) node.unlocked = unlocked;
                if (category != null) node.category = category;
                if (templateId != null) node.templateId = templateId;
                if (dependencies != null) {
                    node.dependencies.clear();
                    node.dependencies.addAll(dependencies);
                }
                if (requirements != null) {
                    node.requirements.clear();
                    node.requirements.putAll(requirements);
                }
                if (teamUnlocked != null) {  // Add this block
                    node.teamUnlocked.clear();
                    node.teamUnlocked.putAll(teamUnlocked);
                }
                System.out.println("Edited node");
                saveNodes(nodeList);
                break;
            }
        }
    }
}
