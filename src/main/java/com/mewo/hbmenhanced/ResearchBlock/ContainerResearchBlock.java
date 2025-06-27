package com.mewo.hbmenhanced.ResearchBlock;

import net.minecraft.block.BlockWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerResearchBlock extends Container {
    private int lastBurnTime;
    private int lastProgress;
    private int tier;
    private TileEntityResearchBlock tileEntity;

    public ContainerResearchBlock(InventoryPlayer inventory, TileEntityResearchBlock te) {
        this.tileEntity = te;
        this.tier = te.tier;
        addSlotToContainer(new Slot(te, 0, 9, 28));
        addSlotToContainer(new Slot(te, 1, 9, 62){
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                // Allow fuel items regardless of tier
                return TileEntityFurnace.getItemBurnTime(itemStack) > 0;
            }
        });
        addSlotToContainer(new Slot(te, 2, 55, 28));

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

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}
