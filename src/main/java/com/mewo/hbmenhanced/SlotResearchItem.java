package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotResearchItem extends Slot {
    private static final getRpValue rpCalculator = new getRpValue();

    public SlotResearchItem(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack == null) return false;

        // Get the research points map for this item
        java.util.EnumMap<getRpValue.researchType, Integer> rpMap = rpCalculator.getRpValuesForItem(stack);

        // If the map is not empty, it means the item has research points in at least one category
        return !rpMap.isEmpty();
    }
}