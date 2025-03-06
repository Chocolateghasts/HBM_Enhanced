package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotResearchItem extends Slot {
    public SlotResearchItem(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return labBlockTileEntity.isResearchItem(stack);
    }
}

