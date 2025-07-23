package com.mewo.hbmenhanced.OpenComputers.old;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mewo.hbmenhanced.getRpValue;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Type;
import java.util.*;
import java.io.*;

public class ResearchTreeold {
    public MinecraftServer server;// = getRpValue.getServer();
    public File worldDir;// = server.getEntityWorld().getSaveHandler().getWorldDirectory();
    public File treeSaveFile;// = new File(worldDir + "/hbmenhanced/tree/nodes/", "Nodes.json");
    private List<ResearchNodeold> nodeList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();

    public ResearchTreeold(MinecraftServer server) {
        this.server = server;
        this.worldDir = server.getEntityWorld().getSaveHandler().getWorldDirectory();
        this.treeSaveFile = new File(worldDir, "hbmenhanced/tree/nodes/Nodes.json");

        getNodes(); // optional preload
    }



    public ResearchNodeold getNode(String id) {
        for (ResearchNodeold node : getNodes()) {
            if (node.id.equalsIgnoreCase(id)) {
                return node;
            }
        }
        return new ResearchNodeold();
    }

    public List<ResearchNodeold> getNodes() {
        try {
            treeSaveFile.getParentFile().mkdirs();
            treeSaveFile.createNewFile();
            Gson gson = new Gson();

            FileReader reader = new FileReader(treeSaveFile);
            Type listType = new TypeToken<ArrayList<ResearchNodeold>>() {}.getType();
            nodeList = gson.fromJson(reader, listType);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Loaded nodes: " + nodeList);
        return nodeList;
    }

    public void saveNodes(List<ResearchNodeold> nodes) {
        try (FileWriter writer = new FileWriter(treeSaveFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            gson.toJson(nodes, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNodes(int id, int amount) {
        List<ResearchNodeold> nodes = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            String nodeId = "node_" + (id + i);

            if (!idList.contains(nodeId)) {
                ResearchNodeold newNode = new ResearchNodeold();
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
            List<Map<String, Object>> templates,
            List<String> dependencies,
            Map<getRpValue.researchType, Integer> requirements,
            Map<String, Boolean> teamUnlocked,
            Float xPos, Float yPos,
            getRpValue.researchType type
    ) {
        getNodes();

        for (ResearchNodeold node : nodeList) {
            if (node.id.equalsIgnoreCase(nodeId)) {
                if (name != null) node.name = name;
                if (description != null) node.description = description;
                if (level != null) node.level = level;
                if (unlocked != null) node.unlocked = unlocked;
                if (category != null) node.category = category;
                if (templates != null) {
                    node.templates.clear();
                    node.templates.addAll(templates);
                }
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
                if (xPos != null) node.xPos = xPos;
                if (yPos != null) node.yPos = yPos;
                if (type != null) node.type = type;
                System.out.println("Edited node");
                saveNodes(nodeList);
                break;
            }
        }
    }

    public List<Map<String, Object>> createTemplates(Object... templateData) {
        List<Map<String, Object>> templates = new ArrayList<>();
        for (int i = 0; i < templateData.length; i += 2) {
            Map<String, Object> template = new HashMap<>();
            template.put("type", templateData[i]);
            template.put("id", templateData[i + 1]);
            templates.add(template);
        }
        return templates;
    }
}
