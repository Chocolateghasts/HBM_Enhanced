package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.Util.getItemValues;
import com.mewo.hbmenhanced.hbmenhanced;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Map;

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
}



