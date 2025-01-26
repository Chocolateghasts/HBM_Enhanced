package com.mewo.hbmenhanced.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class LabBlockTileEntity extends TileEntity implements IInventory {
    private ItemStack[] items = new ItemStack[2]; // Two slots: input (0) and output (1)

    public LabBlockTileEntity() {
        // Ensure that the slots are initialized properly
        items[0] = null; // Input slot
        items[1] = null; // Output slot
    }

    // IInventory methods
    @Override
    public int getSizeInventory() {
        return items.length; // 2 slots
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (items[index] != null) {
            ItemStack itemstack;
            if (items[index].stackSize <= count) {
                itemstack = items[index];
                items[index] = null;
                return itemstack;
            } else {
                itemstack = items[index].splitStack(count);
                if (items[index].stackSize == 0) {
                    items[index] = null;
                }
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        markDirty(); // Mark tile entity as dirty to save changes
    }

    @Override
    public String getInventoryName() {
        return "LabBlock";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64; // Max stack size for items in the inventory
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        // Allow any item in the input slot (slot 0), prevent placing in the output slot (slot 1)
        return index == 0;
    }

    // This method is called every tick
    @Override
    public void updateEntity() {
        if (items[0] != null && items[0].stackSize > 0) {
            // If the output slot is empty, transfer one item from the input to the output
            if (items[1] == null) {
                items[1] = new ItemStack(items[0].getItem(), 1); // Example: Copy 1 item to output
                items[0].stackSize--; // Decrease the input slot by 1 item

                // If the input stack is empty, clear it
                if (items[0].stackSize <= 0) {
                    items[0] = null;
                }

                markDirty(); // Mark the tile entity as updated
            }
        }
    }
}
