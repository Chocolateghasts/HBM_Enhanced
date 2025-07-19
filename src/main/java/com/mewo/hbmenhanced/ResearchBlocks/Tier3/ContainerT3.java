package com.mewo.hbmenhanced.ResearchBlocks.Tier3;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;


public class ContainerT3 extends Container {
    public TileEntityT3 tileEntity;

    public ContainerT3(InventoryPlayer inventory, TileEntityT3 te) {
        this.tileEntity = te;

        addSlotToContainer(new Slot(te, 0, 51, 29));
        addSlotToContainer(new Slot(te, 1, 51, 63){
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return TileEntityFurnace.getItemBurnTime(itemStack) > 0;
//                    if (itemStack.getItem() instanceof IEnergyContainerItem || itemStack.getItem() instanceof ItemBattery || itemStack.getItem() instanceof ItemSelfcharger) {
//                        return true;
//                    }
            }
        });
        addSlotToContainer(new Slot(te, 2, 97, 29));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlotIndex) {
        ItemStack transferredStack = null;
        Slot clickedSlot = (Slot)this.inventorySlots.get(clickedSlotIndex);
        if (clickedSlot != null && clickedSlot.getHasStack()) {
            ItemStack originalStack = clickedSlot.getStack();
            transferredStack = originalStack.copy();
            if (clickedSlotIndex == 2) {
                if (!this.mergeItemStack(originalStack, 3, 39, true)) {
                    return null;
                }

                clickedSlot.onSlotChange(originalStack, transferredStack);
            }
            else if (clickedSlotIndex >= 3 && clickedSlotIndex < 39) {
                if (TileEntityFurnace.isItemFuel(originalStack)) {
                    if (!this.mergeItemStack(originalStack, 1, 2, false)) {
                        return null;
                    }
                }
                else if (tileEntity.isItemValidForSlot(0, originalStack)) {
                    if (!this.mergeItemStack(originalStack, 0, 1, false)) {
                        return null;
                    }
                }
                else if (clickedSlotIndex < 30) {
                    if (!this.mergeItemStack(originalStack, 30, 39, false)) {
                        return null;
                    }
                } else {
                    if (!this.mergeItemStack(originalStack, 3, 30, false)) {
                        return null;
                    }
                }
            }
            else {
                if (!this.mergeItemStack(originalStack, 3, 39, false)) {
                    return null;
                }
            }
            if (originalStack.stackSize == 0) {
                clickedSlot.putStack(null);
            } else {
                clickedSlot.onSlotChanged();
            }
            if (originalStack.stackSize == transferredStack.stackSize) {
                return null;
            }
            clickedSlot.onPickupFromSlot(player, originalStack);
        }
        return transferredStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}
