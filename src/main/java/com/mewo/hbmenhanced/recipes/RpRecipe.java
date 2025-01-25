package com.mewo.hbmenhanced.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RpRecipe implements IRecipe {

    private final ResourceLocation id;

    public RpRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
