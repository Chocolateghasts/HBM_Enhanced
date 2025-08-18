package com.mewo.hbmenhanced.Util.ResearchUtil;

import com.mewo.hbmenhanced.Util.ResearchValue;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class ResearchRegistry {
    public enum MaterialType {INGOT, GEM, PARTICLE, UNKNOWN}
    public enum MaterialRarity {
        COMMON(0), RARE(1), EPIC(2), SPECIAL(3);

        private final int rank;
        MaterialRarity(int rank) { this.rank = rank; }
        public int rank() { return rank; }
    }

    public static Map<Item, MaterialInfo> itemMaterialInfoMap;

    public static void init() {
        itemMaterialInfoMap = new HashMap<>();
        for (Object o : Item.itemRegistry) {
            if (o instanceof Item) {
                Item item = (Item) o;
                itemMaterialInfoMap.put(item, getInfo(item));
            }
        }
    }

    public static boolean canResearch(Item item, int tier) {
        MaterialInfo info = itemMaterialInfoMap.get(item);
        if (info == null) return false;
        if (ResearchItemUtil.getResearchPoints(info) == null) return false;
        if (info.type == MaterialType.UNKNOWN) return false;
        switch (tier) {
            case 1:
                return info.rarity.rank() < MaterialRarity.RARE.rank();
            case 2:
                return info.rarity.rank() < MaterialRarity.EPIC.rank();
            case 3:
                return info.rarity.rank() <= MaterialRarity.SPECIAL.rank();
            default:
                return false;
        }
    }


    public static String getItemName(Item item) {
        return GameRegistry.findUniqueIdentifierFor(item).name.toLowerCase();
    }

    public static MaterialInfo getInfo(Item item) {
        MaterialType type = getMaterialType(item);
        MaterialRarity rarity = getMaterialRarity(item);
        System.out.println("[getInfo] Item=" + item.getUnlocalizedName() + " Rarity=" + rarity + " Type=" + type);
        if (type == null || rarity == null) return null;
        return new MaterialInfo(type, rarity, ResearchItemUtil.getMaterial(item));
    }

    public static MaterialType getMaterialType(Item item) {
        String itemName = getItemName(item);
        if (itemName.contains("ingot")) {
            return MaterialType.INGOT;
        } else if (itemName.contains("gem") && !itemName.contains("tile")) { // Deny tile.
            return MaterialType.GEM;
        } else if (itemName.contains("particle") && !itemName.contains("empty")) {
            return MaterialType.PARTICLE;
        }
        return MaterialType.UNKNOWN;
    }

    public static MaterialRarity getMaterialRarity(Item item) {
        return ResearchItemUtil.rarityMap.getOrDefault(item, MaterialRarity.COMMON);
    }


}
