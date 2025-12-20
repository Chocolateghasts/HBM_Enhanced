package com.mewo.hbmenhanced.blocks.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;

public class TileEntityWashingMachine extends TileEntity implements IInventory {

    private ItemStack[] inventory = new ItemStack[9];

    public int washProgress = 0;
    public int maxWashTime = 200;
    public boolean isWashing = false;

    // ================= INVENTORY =================

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (inventory[slot] != null) {
            ItemStack stack;
            if (inventory[slot].stackSize <= amount) {
                stack = inventory[slot];
                inventory[slot] = null;
                markDirty();
                return stack;
            } else {
                stack = inventory[slot].splitStack(amount);
                if (inventory[slot].stackSize == 0) {
                    inventory[slot] = null;
                }
                markDirty();
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = inventory[slot];
        inventory[slot] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Washing Machine";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
                player.getDistanceSq(
                        xCoord + 0.5D,
                        yCoord + 0.5D,
                        zCoord + 0.5D
                ) <= 64.0D;
    }

    @Override public void openInventory() {}
    @Override public void closeInventory() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    // ================= TICKING =================

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (isWashing) {
                washProgress++;

                if (washProgress >= maxWashTime) {
                    washProgress = 0;
                    isWashing = false;
                }

                markDirty();
            }
        }
    }

    // ================= GUI SCALE =================

    public int getWashTimeScaled(int pixels) {
        return washProgress * pixels / maxWashTime;
    }

    // ================= NBT =================

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        washProgress = nbt.getInteger("WashProgress");
        isWashing = nbt.getBoolean("IsWashing");

        NBTTagList list = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("WashProgress", washProgress);
        nbt.setBoolean("IsWashing", isWashing);

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }

        nbt.setTag("Items", list);
    }
}
