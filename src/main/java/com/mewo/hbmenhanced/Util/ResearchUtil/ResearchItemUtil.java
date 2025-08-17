package com.mewo.hbmenhanced.Util.ResearchUtil;

import com.hbm.items.ModItems;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

import static com.mewo.hbmenhanced.Util.ResearchValue.mapOf;
import static com.mewo.hbmenhanced.Util.ResearchValue.pairOf;
import static com.mewo.hbmenhanced.ResearchManager.PointManager.ResearchType.*;

public class ResearchItemUtil {

    public static Map<String, ResearchValue> materialRegistry = new HashMap<>();
    public static Map<Item, String> itemMaterialRegistry = new HashMap<>();

    public static void value(String material, Map<PointManager.ResearchType, Integer> m, int r) {
        ResearchValue value = new ResearchValue(m, r);
        materialRegistry.put(material, value);
    }

    public static void registerItem(Item item, String material) {
        itemMaterialRegistry.put(item, material);
    }
    // TODO: Call initMaterials BEFORE init(). Otherwise it will break!
    public static void initMaterials() {
        registerItem(Items.iron_ingot, "iron");
        registerItem(Items.gold_ingot, "gold");
        registerItem(ModItems.ingot_copper, "copper");
        registerItem(ModItems.ingot_red_copper, "red_copper");
        registerItem(ModItems.ingot_steel, "steel");
        registerItem(ModItems.ingot_steel_dusted, "dusted_steel");
        registerItem(ModItems.ingot_dura_steel, "durasteel");
        registerItem(ModItems.ingot_u238, "u238");
        registerItem(ModItems.ingot_u235, "u235");
        registerItem(ModItems.ingot_u233, "u233");
        registerItem(ModItems.ingot_uranium, "uranium");
        registerItem(ModItems.ingot_technetium, "technetium");
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
        return materialRegistry.getOrDefault(material.toLowerCase(), new ResearchValue());
    }

    public static ResearchValue getResearchValue(Item item) {
        MaterialInfo info = ResearchRegistry.itemMaterialInfoMap.get(item);
        if (info == null) return new ResearchValue();
        return getResearchPoints(info);
    }
}
