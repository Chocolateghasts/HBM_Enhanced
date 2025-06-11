package com.mewo.hbmenhanced.ReactorResearch;

import com.mewo.hbmenhanced.items.ItemLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerResearchCore extends Container {
    private TileEntityResearchCore tileEntity;

    public ContainerResearchCore(InventoryPlayer inventory, TileEntityResearchCore te) {
        this.tileEntity = te;

        addSlotToContainer(new Slot(te, 0,12, 10)
        {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem() instanceof ItemLink;
            }

            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
            }
        });
        addSlotToContainer(new Slot(te, 1,12, 30));
        addSlotToContainer(new Slot(te, 2,12, 50));

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
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex < tileEntity.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, tileEntity.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
