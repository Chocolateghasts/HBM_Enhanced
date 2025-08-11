package com.mewo.hbmenhanced.OpenComputers;

import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.ResearchManager.PointManager.ResearchType;
import com.mewo.hbmenhanced.Util.ResearchTemplate;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.recipes.ServerTemplates;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchNode {
    public String id;
    public String name;
    public String category;
    public String description;
    public String iconId;
    public String backgroundId;
    public int level;
    public int x;
    public int y;
    public boolean isUnlocked;
    public String[] dependencies;
    public List<ResearchTemplate> templates;
    public Map<String, Integer> requirements;

    public ResearchNode() {}

    public ResearchNode(String id) {
        this.id = id;
    }
// TODO: Add dependency check
    public Result unlock(String team, World world) {
        ResearchTree thisTree = ResearchTree.getTree(team);
        if (this.isUnlocked) {
            return new Result(false, "Node is already unlocked");
        }
        if (world == null) {
            return new Result(false, "World is null");
        }
        for (String dep : dependencies) {
            ResearchNode node = thisTree.getNode(dep);
            if (node == null) {
                return new Result(false, "you wrote a non existent dependency or the tree is half initialized");
            }
            if (!node.isUnlocked) {
                return new Result(false, "Dependency " + node.name + " is not unlocked!");
            }
        }
        for (Map.Entry<String, Integer> entry : this.requirements.entrySet()) {
            ResearchType type = ResearchType.valueOf(entry.getKey());
            int points = PointManager.getPoints(team, type);
            System.out.println("Type: " + type + "- Points: " + points);
            if (points < entry.getValue()) {
                return new Result(false, "Not enough " + type.name().toLowerCase() + " points");
            }
        }
        for (Map.Entry<String, Integer> entry : this.requirements.entrySet()) {
            ResearchType type = ResearchType.valueOf(entry.getKey());
            PointManager.addPoints(team, type, -entry.getValue(), world);
        }
        this.isUnlocked = true;
        thisTree.markDirty();
        thisTree.save();
        ServerTemplates.markDirty();
        return new Result(true, "Successfully unlocked node!");
    }
}