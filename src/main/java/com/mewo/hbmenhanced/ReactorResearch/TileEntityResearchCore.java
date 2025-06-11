package com.mewo.hbmenhanced.ReactorResearch;

import com.hbm.tileentity.machine.TileEntityReactorResearch;
import com.mewo.hbmenhanced.items.ItemLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityResearchCore extends TileEntity implements IInventory {

    protected ItemStack[] inventory;
    public static final int INVENTORY_SIZE = 3;
    private float stabilityTimer = 0;

    public TileEntityResearchCore() {
        inventory = new ItemStack[INVENTORY_SIZE];
    }

    @Override
    public void updateEntity() {
        TileEntityReactorResearch reactor = getReactor();
        if (reactor != null) {
            System.out.println("Got reactor!");
        }
    }

    private TileEntityReactorResearch getReactor() {
        if (inventory[0] != null && inventory[0].getItem() instanceof ItemLink) {
            ItemStack itemStack = inventory[0];
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (nbt != null) {
                int x = nbt.getInteger("hbmenhanced:linkedX");
                int y = nbt.getInteger("hbmenhanced:linkedY");
                int z = nbt.getInteger("hbmenhanced:linkedZ");
                TileEntity te = worldObj.getTileEntity(x, y, z);
                if (te instanceof TileEntityReactorResearch) {
                    return (TileEntityReactorResearch) te;
                }
            }
        }
        return null;
    }

    private void analyseReactor(TileEntityReactorResearch reactor) {
        int heat = reactor.heat;
        int flux = reactor.totalFlux;
        int water = reactor.getWater();
        int maxHeat = reactor.maxHeat;

        boolean isStable = true;

        if (heat > maxHeat * 0.8) {
            isStable = false;
        }

        if (isStable) {
            stabilityTimer += 0.05F;
        }

    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (inventory[slot] != null) {
            ItemStack itemStack;
            if (inventory[slot].stackSize <= count) {
                itemStack = inventory[slot];
                inventory[slot] = null;
                markDirty();
                return itemStack;
            } else {
                itemStack = inventory[slot].splitStack(count);
                if (inventory[slot].stackSize == 0) {
                    inventory[slot] = null;
                }
                markDirty();
                return itemStack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (inventory[slot] != null) {
            ItemStack itemstack = inventory[slot];
            inventory[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        inventory[slot] = itemStack;
        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.researchCore";
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
                player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        if (slot == 0) {
            if (itemStack.getItem() instanceof ItemLink ) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagCompound items = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                items.setTag("Slot" + i, item);
            }
        }
        compound.setTag("Items", items);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagCompound items = compound.getCompoundTag("Items");
        for (int i = 0; i < inventory.length; i++) {
            if (items.hasKey("Slot" + i)) {
                NBTTagCompound item = items.getCompoundTag("Slot" + i);
                inventory[i] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }
}