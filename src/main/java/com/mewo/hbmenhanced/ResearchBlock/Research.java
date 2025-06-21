package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.getItemValues;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class Research {
    public void Tier1(ItemStack[] inventory, int mainSlot, int fuelSlot, TileEntityResearchBlock te) {
        if (te.getWorldObj().isRemote) return;
        ItemStack input = inventory[mainSlot];
        if (input == null) {
            te.isResearching = false;
            return;
        }

        ItemStack fuel = inventory[fuelSlot];
        if (!te.isResearching && te.currentBurnTime <= 0) {
            int burnTime = (fuel != null) ? TileEntityFurnace.getItemBurnTime(fuel) : 0;

            if (burnTime <= 0) {
                return;
            }

            te.currentBurnTime = burnTime;

            if (--fuel.stackSize <= 0) {
                inventory[fuelSlot] = fuel.getItem().getContainerItem(fuel);
            }

            te.isResearching = true;
            te.maxResearchProgress = 200;
            te.researchProgress = 0;
        }
        if (te.isResearching) {
            if (te.currentBurnTime > 0) {
                te.currentBurnTime--;
                te.researchProgress++;

                if (te.researchProgress >= te.maxResearchProgress) {

                    ResearchValue points = getItemValues.getPoints(input);
                    if (--input.stackSize <= 0) {
                        inventory[mainSlot] = null;
                    }

                    if (points != null) {
                        PointManager.addPoints(te.getTeam(), points.getType(), points.getPoints());
                    }

                    te.isResearching = false;
                }
            } else {
                te.isResearching = false;
            }
        }
        te.markDirty();
    }
}
