package com.mewo.hbmenhanced.Util.ResearchUtil;

import com.hbm.items.ModItems;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

import static com.mewo.hbmenhanced.Util.ResearchUtil.ResearchRegistry.MaterialRarity.*;
import static com.mewo.hbmenhanced.Util.ResearchValue.mapOf;
import static com.mewo.hbmenhanced.Util.ResearchValue.pairOf;
import static com.mewo.hbmenhanced.ResearchManager.PointManager.ResearchType.*;

public class ResearchItemUtil {

    public static Map<String, ResearchValue> materialRegistry = new HashMap<>();
    public static Map<Item, String> itemMaterialRegistry = new HashMap<>();
    public static Map<Item, ResearchRegistry.MaterialRarity> rarityMap = new HashMap<>();

    public static void value(String material, Map<PointManager.ResearchType, Integer> m, int r) {
        ResearchValue value = new ResearchValue(m, r);
        materialRegistry.put(material, value);
    }

    public static void registerItem(Item item, String material, ResearchRegistry.MaterialRarity rarity) {
        itemMaterialRegistry.put(item, material);
        rarityMap.put(item, rarity);
    }
    // TODO: Call initMaterials BEFORE init(). Otherwise it will break!
    public static void initMaterials() {
        registerItem(Items.iron_ingot, "iron", COMMON);
        registerItem(Items.gold_ingot, "gold", RARE);
        registerItem(ModItems.ingot_copper, "copper", COMMON);
        registerItem(ModItems.ingot_red_copper, "red_copper", COMMON);
        registerItem(ModItems.ingot_steel, "steel", COMMON);
        registerItem(ModItems.ingot_steel_dusted, "dusted_steel", EPIC);
        registerItem(ModItems.ingot_dura_steel, "durasteel", RARE);
        registerItem(ModItems.ingot_u238, "u238", EPIC);
        registerItem(ModItems.ingot_u235, "u235", EPIC);
        registerItem(ModItems.ingot_u233, "u233", EPIC);
        registerItem(ModItems.ingot_uranium, "uranium", EPIC);
        registerItem(ModItems.ingot_technetium, "technetium", EPIC);
        registerItem(ModItems.particle_copper, "apple", EPIC);
    }

    public static void init() {
        value("iron", mapOf(
                pairOf(STRUCTURAL, 10)
        ), 540);
        value("steel", mapOf(
                pairOf(STRUCTURAL, 20),
                pairOf(SPACE, 5)
        ), 600);
        value("copper", mapOf(
                pairOf(ELECTRONICS, 20)
        ), 320);
        value("gold", mapOf(
                pairOf(ELECTRONICS, 25)
        ), 500);
        value("durasteel", mapOf(
                pairOf(WEAPONRY, 20)
        ), 600);
        value("red_copper", mapOf(
                pairOf(ELECTRONICS, 40)
        ), 460);
        value("dusted_steel", mapOf(
                pairOf(STRUCTURAL, 20)
        ), 700);
        value("uranium", mapOf(
                pairOf(NUCLEAR, 15)
        ), 600);
        value("u238", mapOf(
                pairOf(WEAPONRY, 25),
                pairOf(NUCLEAR, 10)
        ), 800);
        value("u235", mapOf(
                pairOf(NUCLEAR, 30)
        ), 800);
        value("u233", mapOf(
                pairOf(NUCLEAR, 35)
        ), 840);
        value("technetium", mapOf(
                pairOf(STRUCTURAL, 25)
        ), 680);
        value("apple", mapOf(
                pairOf(STRUCTURAL, 69)
        ), 25);
    }

    public static String getMaterial(Item item) {
        return itemMaterialRegistry.getOrDefault(item, "unknown");
    }

    public static ResearchValue getResearchPoints(MaterialInfo info) {
        ResearchValue base = getBasePointsForMaterial(info.material);
        if (base == null) return null;

        ResearchValue val = base.copy();
        switch (info.type) {
            case INGOT:     val.multiply(1); break;
            case GEM:       val.multiply(2); break;
            case PARTICLE:  val.multiply(5); break;
            case UNKNOWN:   val.multiply(0); break;
        }
        return val;
    }


    public static ResearchValue getBasePointsForMaterial(String material) {
        return materialRegistry.get(material.toLowerCase());
    }

    public static ResearchValue getResearchValue(Item item) {
        MaterialInfo info = ResearchRegistry.itemMaterialInfoMap.get(item);
        if (info == null) return new ResearchValue();
        return getResearchPoints(info);
    }
}
