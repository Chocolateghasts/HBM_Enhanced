package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.getItemValues;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

public class Research {

    public void Tier1(ItemStack[] inventory, int mainSlot, int fuelSlot, TileEntityResearchBlock te) {
        if (te.getWorldObj().isRemote) return;

        ItemStack input = inventory[mainSlot];
        ItemStack fuel = inventory[fuelSlot];

        if (input == null) {
            te.isResearching = false;
            return;
        }

        if (!te.isResearching && te.currentBurnTime <= 0) {
            if (fuel != null && TileEntityFurnace.getItemBurnTime(fuel) > 0) {
                te.currentBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
                fuel.stackSize--;
                if (fuel.stackSize <= 0) {
                    inventory[fuelSlot] = fuel.getItem().getContainerItem(fuel);
                }
                te.isResearching = true;
                te.maxResearchProgress = 200;
            } else {
                return;
            }
        }

        if (te.isResearching) {
            if (te.currentBurnTime > 0) {
                te.currentBurnTime--;
                te.researchProgress++;

                if (te.researchProgress >= te.maxResearchProgress) {
                    ResearchValue points = getItemValues.getPoints(input);
                    input.stackSize--;
                    if (input.stackSize <= 0) {
                        inventory[mainSlot] = null;
                    }

                    te.researchProgress = 0;
                    te.maxResearchProgress = 0;
                    te.isResearching = false;
                }
            } else {
                te.isResearching = false;
            }
        }

        te.markDirty();
    }
}