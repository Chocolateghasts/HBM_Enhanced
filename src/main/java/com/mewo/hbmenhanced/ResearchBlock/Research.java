package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.Util.getItemValues;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

public class Research {

    public void Tier1(ItemStack[] inventory, int mainSlot, int fuelSlot, World world, TileEntityResearchBlock te) {
        if (!world.isRemote) {
            ItemStack researchable = inventory[mainSlot];
            ItemStack fuel = inventory[fuelSlot];

            if (getItemValues.isResearchItem(researchable.getUnlocalizedName())) {
                int burnTime = TileEntityFurnace.getItemBurnTime(fuel);
                int researchTime = getItemValues.getResearchTime(researchable);
                if (burnTime > 0 && researchTime > 0) {
                    burnTime -= 1;
                    researchTime -=1;
                }
                if (burnTime == 0) {

                }
                if (researchTime == 0) {

                }
            }
        }
    }
}