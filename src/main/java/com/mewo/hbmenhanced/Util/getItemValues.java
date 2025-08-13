package com.mewo.hbmenhanced.Util;

import com.hbm.items.ModItems;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.TileEntityResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.*;

public class getItemValues {

    public static Map<String, Map<String, Integer>> researchCounts = new HashMap<>();
    public static Map<String, ResearchValue> Values = new HashMap<>();

    // Creates a truly unique identifier for an item including metadata
    private static String getUniqueItemKey(Item item, int meta) {
        return Item.itemRegistry.getNameForObject(item) + ":" + meta;
    }

    private static String getUniqueItemKey(ItemStack stack) {
        return getUniqueItemKey(stack.getItem(), stack.getItemDamage());
    }

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
                "flint", "furnace"
        };
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

    public static ResearchValue getItemValue(ItemStack stack) {
        String key = getUniqueItemKey(stack); // uses damage/meta from stack
        System.out.println("Item is: " + key);
        if (Values.containsKey(key)) {
            System.out.println("Found item in map");
        }
        return Values.getOrDefault(key, new ResearchValue());
    }

    public static int getResearchTime(TileEntity te) {
        int tier = TileEntityResearchController.getTierOfBlockStatic(te);
        TileEntityResearchController core = getCore(te);
        if (core == null) return 240;

        float multiplier = core.researchTimeMultiplier;
        float total = 240;

        switch (tier) {
            case 1:
                total = 240 / multiplier;
                break;
            case 2:
                total = 120 / multiplier;
                break;
            case 3:
                total = 60 / multiplier;
                break;
        }

        return (int) total;
    }

    private static TileEntityResearchController getCore(TileEntity te) {
        if (te instanceof TileEntityT1) {
            return ((TileEntityT1) te).core;
        } else if (te instanceof TileEntityT2) {
            return ((TileEntityT2) te).core;
        } else if (te instanceof TileEntityT3) {
            return ((TileEntityT3) te).core;
        }
        return null;
    }

    public static void init() {
        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            String rawName = item.getUnlocalizedName().toLowerCase();
            String normalizedName = rawName
                    .replace("tile.", "")
                    .replace("item.", "")
                    .replace("_", " ")
                    .trim();

            if (!isResearchItem(normalizedName)) continue;

            ResearchValue value = new ResearchValue();
            for (Map.Entry<String, ResearchValue> entry : ResearchMap.keywordMap.entrySet()) {
                String keyword = entry.getKey().toLowerCase();
                if (normalizedName.contains(keyword)) {
                    System.out.println("Registered " + normalizedName);
                    System.out.println(normalizedName + " contains " + keyword);
                    ResearchValue toAdd = entry.getValue();
                    for (Map.Entry<PointManager.ResearchType, Integer> point : toAdd.getAllPoints().entrySet()) {
                        value.addPoints(point.getKey(), point.getValue());
                    }
                }
            }

            if (!value.getAllPoints().isEmpty()) {
                String key = getUniqueItemKey(item, 0); // Default meta for base item
                System.out.println("Added " + key);
                Values.put(key, value);
            }
        }
        System.out.println("VALUES OF RESEARCHMAPIDK: " + Values.keySet());
    }

    private static int getDiminishedValues(String itemKey, int base, String team) {
        researchCounts.computeIfAbsent(team, t -> new HashMap<>());
        int count = researchCounts.get(team).getOrDefault(itemKey, 0);
        return Math.max(1, base / (1 + count));
    }

    public static void setValues(ResearchValue value, ItemStack outStack, String team, ItemStack sourceStack) {
        if (!outStack.hasTagCompound()) {
            outStack.setTagCompound(new NBTTagCompound());
        }

        if (sourceStack == null) {
            System.out.println("sourceStack is null, cannot apply diminishing correctly");
            return;
        }

        String sourceKey = getUniqueItemKey(sourceStack); // << use this for diminishing
        System.out.println("Got source item key for diminishing: " + sourceKey + " for team: " + team);

        ItemResearchPoint.setRp(outStack, "CHEMICAL", 0);
        System.out.println("setValues called for: " + outStack);

        if (value == null) {
            System.out.println("value is null!");
            return;
        }

        Map<PointManager.ResearchType, Integer> map = value.getAllPoints();
        System.out.println("Map size: " + map.size());
        researchCounts.computeIfAbsent(team, t -> new HashMap<>());

        for (Map.Entry<PointManager.ResearchType, Integer> entry : map.entrySet()) {
            int diminishedValue = getDiminishedValues(sourceKey, entry.getValue(), team);
            outStack.getTagCompound().setInteger(entry.getKey().toString(), diminishedValue);
        }

        int current = researchCounts.get(team).getOrDefault(sourceKey, 0);
        researchCounts.get(team).put(sourceKey, current + 1);
        System.out.println("Applied diminishing for source item: " + sourceKey + " for team: " + team);
    }
}
