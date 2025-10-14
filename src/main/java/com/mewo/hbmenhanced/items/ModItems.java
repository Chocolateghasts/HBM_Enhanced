package com.mewo.hbmenhanced.items;

import com.mewo.hbmenhanced.blocks.LabBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class ModItems {
    public static Item ITEM_SPEED_1;
    public static Item ITEM_SPEED_2;
    public static Item ITEM_SPEED_3;
    public static Item ITEM_POWER_1;
    public static Item ITEM_POWER_2;
    public static Item ITEM_POWER_3;

    public static void init() {
        ITEM_POWER_1 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.POWER, 1).setUnlocalizedName("power_upgrade_1");
        ITEM_POWER_2 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.POWER, 2).setUnlocalizedName("power_upgrade_2");
        ITEM_POWER_3 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.POWER, 3).setUnlocalizedName("power_upgrade_3");
        ITEM_SPEED_1 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.SPEED, 1).setUnlocalizedName("speed_upgrade_1");
        ITEM_SPEED_2 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.SPEED, 2).setUnlocalizedName("speed_upgrade_2");
        ITEM_SPEED_3 = new ItemResearchUpgrade(ItemResearchUpgrade.UpgradeType.SPEED, 3).setUnlocalizedName("speed_upgrade_3");
    }

    public static void register() {
        GameRegistry.registerItem(ITEM_POWER_1, ITEM_POWER_1.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ITEM_POWER_2, ITEM_POWER_2.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ITEM_POWER_3, ITEM_POWER_3.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ITEM_SPEED_1, ITEM_SPEED_1.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ITEM_SPEED_2, ITEM_SPEED_2.getUnlocalizedName().substring(5));
        GameRegistry.registerItem(ITEM_SPEED_3, ITEM_SPEED_3.getUnlocalizedName().substring(5));
    }
}
