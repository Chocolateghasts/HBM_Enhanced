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

    public static Map<String, ResearchValue> Values = new HashMap<>();

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
    public static ResearchValue getItemValue(Item item) {
        System.out.println("Item is: " + item.getUnlocalizedName());
        if (Values.containsKey(item.getUnlocalizedName().toLowerCase())) {
            System.out.println("Found item in map");
        }
        return Values.getOrDefault(item.getUnlocalizedName().toLowerCase(), new ResearchValue());
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
        TileEntityResearchController core = null;
        if (te instanceof TileEntityT1) {
            if (((TileEntityT1) te).core != null) {
                core = ((TileEntityT1) te).core;
            }
        } else if (te instanceof TileEntityT2) {
            if (((TileEntityT2) te).core != null) {
                core = ((TileEntityT2) te).core;
            }
        } else if (te instanceof TileEntityT3) {
            if (((TileEntityT3) te).core != null) {
                core = ((TileEntityT3) te).core;
            }
        }
        return core;
    }

    public static void init() {
        for (Object obj : Item.itemRegistry) {

            Item item = (Item) obj;
            String name = item.getUnlocalizedName().toLowerCase();

            if (!isResearchItem(name)) continue;
            ResearchValue value = new ResearchValue();
            for (Map.Entry<String, ResearchValue> entry : ResearchMap.keywordMap.entrySet()) {
                String keyword = entry.getKey().toLowerCase();
                if (name.contains(keyword)) {
                    System.out.println("Registered " + name);
                    System.out.println(name + " contains " + keyword);
                    ResearchValue toAdd = entry.getValue();
                    for (Map.Entry<PointManager.ResearchType, Integer> point : toAdd.getAllPoints().entrySet()) {
                        value.addPoints(point.getKey(), point.getValue());
                    }
                }
            }

            if (!value.getAllPoints().isEmpty()) {
                System.out.println("Added " + item.getUnlocalizedName());
                Values.put(item.getUnlocalizedName().toLowerCase(), value);
            }

        }
        System.out.println("VALUES OF RESEARCHMAPIDK: " + Values.keySet());
    }

    public static void setValues(ResearchValue value, ItemStack stack) {
        ItemResearchPoint.setRp(stack, "CHEMICAL", 0);
        System.out.println("setValues called for: " + stack);
        if (value == null) {
            System.out.println("value is null!");
            return;
        }
        Map<PointManager.ResearchType, Integer> map = value.getAllPoints();
        System.out.println("Map size: " + map.size());
        for (Map.Entry<PointManager.ResearchType, Integer> entry : map.entrySet()) {
            stack.getTagCompound().setInteger(entry.getKey().toString(), entry.getValue());
        }
    }
}