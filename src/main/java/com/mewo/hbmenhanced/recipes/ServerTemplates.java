package com.mewo.hbmenhanced.recipes;

import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.Packets.PacketTemplates;
import com.mewo.hbmenhanced.TeamData;
import com.mewo.hbmenhanced.Util.ResearchTemplate;

import java.util.*;

public class ServerTemplates {
    public static int version;
    public static Map<ResearchTemplate, GenericRecipe> templateRecipeMap = new HashMap<>();
    public static Map<String, Set<ResearchTemplate>> teamTemplates = new HashMap<>();

    public static void preInit() {
        Map<ResearchTemplate, Boolean> assemblyTemplates = new HashMap<>();
        Map<ResearchTemplate, Boolean> chemTemplates = new HashMap<>();

        for (GenericRecipe recipe : AssemblyMachineRecipes.INSTANCE.recipeOrderedList) {
            ResearchTemplate template = new ResearchTemplate("a", recipe.getInternalName());
            templateRecipeMap.put(template, recipe);
        }
        for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
            ResearchTemplate template = new ResearchTemplate("b", recipe.getInternalName());
            templateRecipeMap.put(template, recipe);
        }
    }

    public static void init() {
        for (Map.Entry<String, ResearchTree> entry : ResearchTree.trees.entrySet()) {
            String team = entry.getKey();
            ResearchTree tree = entry.getValue();

            Set<ResearchTemplate> unlockedTemplates = teamTemplates.computeIfAbsent(team, k -> new HashSet<>());

            for (ResearchNode node : tree.nodes.values()) {
                if (node.isUnlocked) {
                    unlockedTemplates.addAll(node.templates);
                }
            }
        }
    }

    public static void update(String team, ResearchNode node) {
        if (node.isUnlocked) {
            Set<ResearchTemplate> unlocked = teamTemplates.computeIfAbsent(team, k -> new HashSet<>());
            unlocked.addAll(node.templates);
            markDirty();
        }
    }

    public static Set<ResearchTemplate> getUnlockedTemplates(String team) {
        return teamTemplates.getOrDefault(team, Collections.emptySet());
    }

    public static void clearTemplates(String team) {
        teamTemplates.remove(team);
    }

    public static void reset() {
        teamTemplates.clear();
    }

    public static void markDirty() {
        version++;
    }

    public static PacketTemplates fullSync(String team) {
        Set<ResearchTemplate> templates = teamTemplates.get(team);
        if (templates == null) templates = Collections.emptySet();
        return new PacketTemplates(PacketTemplates.PacketType.FULL_SYNC, version, templates);
    }
}
