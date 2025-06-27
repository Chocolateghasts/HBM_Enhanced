package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.getItemValues;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class Research {
    public void Tier1(ItemStack[] inventory, int mainSlot, int fuelSlot, int outputSlot, TileEntityResearchBlock te) {
        if (te.getWorldObj().isRemote) return;

        ItemStack input = inventory[mainSlot];
        ItemStack fuel = inventory[fuelSlot];

        if (input == null) {
            te.isResearching = false;
            te.researchProgress = 0;
            te.maxResearchProgress = 0;
            return;
        }

        if (!te.isResearching) {
            te.isResearching = true;
            te.researchProgress = 0;
            te.maxResearchProgress = getItemValues.getResearchTime(input);
        }

        if (te.currentBurnTime > 0) {
            te.currentBurnTime--;
            te.isBurning = true;
        } else if (fuel != null) {
            te.currentBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
            if (te.currentBurnTime > 0) {
                fuel.stackSize--;
                if (fuel.stackSize <= 0) inventory[fuelSlot] = null;
                te.isBurning = true;
            } else {
                te.isBurning = false;
            }
        } else {
            te.isBurning = false;
        }

        if (te.isBurning && te.isResearching) {
            te.researchProgress++;
            if (te.researchProgress >= te.maxResearchProgress) {
                te.isResearching = false;
                te.researchProgress = 0;
                te.maxResearchProgress = 0;
                ResearchValue value = getItemValues.getPoints(input);
                PointManager.addPoints(te.getTeam(), value.getType(), value.getPoints());
            }
        }
    }

}

//    public void Tier1(ItemStack[] inventory, int mainSlot, int fuelSlot, TileEntityResearchBlock te) {
//        if (te.getWorldObj().isRemote) return;
//        ItemStack input = inventory[mainSlot];
//        if (input == null) {
//            te.isResearching = false;
//            return;
//        }
//
//        ItemStack fuel = inventory[fuelSlot];
//        if (!te.isResearching && te.currentBurnTime <= 0) {
//            int burnTime = (fuel != null) ? TileEntityFurnace.getItemBurnTime(fuel) : 0;
//
//            if (burnTime <= 0) {
//                return;
//            }
//
//            te.currentBurnTime = burnTime;
//
//            if (--fuel.stackSize <= 0) {
//                inventory[fuelSlot] = fuel.getItem().getContainerItem(fuel);
//            }
//
//            te.isResearching = true;
//            te.maxResearchProgress = 200;
//            te.researchProgress = 0;
//        }
//        if (te.isResearching) {
//            if (te.currentBurnTime > 0) {
//                te.currentBurnTime--;
//                te.researchProgress++;
//
//                if (te.researchProgress >= te.maxResearchProgress) {
//
//                    ResearchValue points = getItemValues.getPoints(input);
//                    if (--input.stackSize <= 0) {
//                        inventory[mainSlot] = null;
//                    }
//
//                    if (points != null) {
//                        PointManager.addPoints(te.getTeam(), points.getType(), points.getPoints());
//                    }
//
//                    te.isResearching = false;
//                }
//            } else {
//                te.isResearching = false;
//            }
//        }
//        te.markDirty();
//    }