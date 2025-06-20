package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.getItemValues;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class Research {

    private void Log(String text) {
        System.out.println(text);
    }

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
                ItemStack containerItem = fuel.getItem().getContainerItem(fuel);

                fuel.stackSize--;
                if (fuel.stackSize <= 0) {
                    inventory[fuelSlot] = containerItem;
                }
                te.isResearching = true;
                te.maxResearchProgress = 200;
            } else {
                Log("Fuel null");
                return;
            }
        }

        if (te.isResearching) {
            if (te.currentBurnTime > 0) {
                te.currentBurnTime--;
                te.researchProgress++;

                if (te.researchProgress >= te.maxResearchProgress) {
                    Log("Completed");
                    input.stackSize--;
                    if (input.stackSize <= 0) {
                        inventory[mainSlot] = null;
                    }

                    if (te.researchProgress >= te.maxResearchProgress) {
                        ResearchValue points = getItemValues.getPoints(input);
                        if (points != null && te.getTeam() != null && !te.getTeam().isEmpty()) {
                            input.stackSize--;
                            if (input.stackSize <= 0) {
                                inventory[mainSlot] = null;
                            }

                            PointManager.addPoints(te.getTeam(), points.getType(), points.getPoints());
                            te.researchProgress = 0;
                            te.maxResearchProgress = 0;
                            te.isResearching = false;
                        }
                    }
                    te.researchProgress = 0;
                    te.maxResearchProgress = 0;
                    te.isResearching = false;
                }
            } else {
                Log("No burntime");
                te.isResearching = false;
            }
        }

        te.markDirty();
    }
}