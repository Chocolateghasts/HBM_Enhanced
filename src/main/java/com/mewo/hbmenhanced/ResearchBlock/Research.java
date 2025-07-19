package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.Util.getItemValues;
import com.mewo.hbmenhanced.hbmenhanced;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Map;

// TODO: Research May Produce Waste Or Fail Research
// TODO: Tier 1 May Only Research Low Level Items

public class Research {
    public void Tier1(TileEntityT1 te) {
        int mainSlot = te.MAIN_SLOT;
        int outputSlot = te.OUTPUT_SLOT;
        int fuelSlot = te.FUEL_SLOT;
        ItemStack[] inventory = te.inventory;

        ItemStack input = inventory[mainSlot];
        ItemStack output = inventory[outputSlot];
        ItemStack fuel = inventory[fuelSlot];

        if (output != null) return;

        if (input == null) {
            te.isResearching = false;
            te.researchProgress = 0;
            te.maxResearchProgress = 0;
        } else {
            te.isResearching = true;
            if (te.researchProgress == 0) {
                te.maxResearchProgress = 60;
            }
        }

        if (te.isResearching) {
            if (te.currentBurnTime <= 0) te.isBurning = false;
            if (fuel != null && !te.isBurning) {
                te.isBurning = true;
                te.totalBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
                te.currentBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
                fuel.stackSize--;
                inventory[fuelSlot] = fuel.stackSize <= 0 ? null : fuel;
            }
            if (te.currentBurnTime > 0) {
                te.currentBurnTime--;
                te.researchProgress++;
                if (te.researchProgress >= te.maxResearchProgress) {
                    te.researchProgress = 0;
                    te.maxResearchProgress = 0;
                    input.stackSize--;
                    inventory[mainSlot] = input.stackSize <= 0 ? null : input;
                    ItemStack itemStack = new ItemStack(hbmenhanced.researchPoint, 1);
                    ResearchValue researchValue = getItemValues.getItemValue(input.getItem());
                    getItemValues.setValues(researchValue, itemStack);
                    output = itemStack;
                    inventory[outputSlot] = output;
                }
            }
        }
    }

    public void Tier2(TileEntityT2 te) {
        int mainSlot = te.MAIN_SLOT;
        int outputSlot = te.OUTPUT_SLOT;
        ItemStack[] inventory = te.inventory;

        ItemStack input = inventory[mainSlot];
        ItemStack output = inventory[outputSlot];
        if (output != null) {
            System.out.println("Early Return");
            te.isBurning = false;
            return;
        }


        if (input == null) {
            System.out.println("Early Return");
            te.isBurning = false;
            return;
        } 


        if (!te.isResearching) {

            if (te.tank.getFill() >= 100) {
                System.out.println("Starting Research");
                te.tank.setFill(te.tank.getFill() - 100);
                te.isResearching = true;
                te.maxResearchProgress = getItemValues.getResearchTime(2);
                te.isBurning = true;
            } else {
                te.isBurning = false;
            }
        }
        if (te.isResearching && te.tank.getFill() >= 100) {
            te.researchProgress++;
            te.tank.setFill(te.tank.getFill() - 100);
            te.isBurning = true;
            if (te.researchProgress >= te.maxResearchProgress) {
                System.out.println("Research Finished");
                //Award Points
                ResearchValue val = getItemValues.getItemValue(input.getItem());
                ItemStack point = new ItemStack(hbmenhanced.researchPoint);
                getItemValues.setValues(val, point);
                te.inventory[outputSlot] = point;
                te.researchProgress = 0;
                te.isResearching = false;
                te.inventory[mainSlot].stackSize--;
                if (te.inventory[mainSlot].stackSize <= 0) {
                    te.inventory[mainSlot] = null;
                }
            }
        } else {
            te.isBurning = false;
        }

    }
}



