package com.mewo.hbmenhanced.Util;

import net.minecraft.item.Item;

import java.util.*;
public class getItemValues {

    public static Map<Item, ResearchValue> Values;

    public static boolean isBlackListed(String key) {
        String[] keywords = {
                "ladder", "plate", "rod", "pipe",
                "duct", "shell", "wire", "coil",
                "powder", "gear", "barrel",
                "receiver", "stock", "grip",
                "mechanism", "fin", "blade",
                "flywheel", "pole", "anvil",
                "sphere", "beam", "wall", "block",
                "sword", "shovel", "hoe", "pickaxe",
                "drill", "crate", "ore", "billet",
                "helmet", "chestplate", "leggings",
                "box", "boots", "armor", "pylon",
                "capacitor", "crystal", "deco",
                "glass", "bars", "door", "sand",
                "axe", "tank", "scaffold", "corner",
                "pedestal", "port", "carrot", "tile",
                "flint", "furnace"};
        for (String keyword : keywords) {
            if (key.toLowerCase().trim().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResearchItem(String name) {
        return !isBlackListed(name);
    }

    public ResearchValue getItemValue(Item item) {
        return new ResearchValue();
    }

    public static void init() {
        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            if (isResearchItem(item.getUnlocalizedName())) {
                //Values.put(item, )
            }
        }
    }
}