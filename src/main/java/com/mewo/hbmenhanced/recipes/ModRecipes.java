package com.mewo.hbmenhanced.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class ModRecipes {
    public static void createRecipe(ItemStack itemstack) {
        Object[] recipe = new Object[]{};

        GameRegistry.addRecipe(itemstack);
    }

    public static void Init() {
//        GameRegistry.addRecipe();
    }
}
