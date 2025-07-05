package com.mewo.hbmenhanced.ResearchBlock.Tier2;

import com.mewo.hbmenhanced.Packets.ResearchTier1Packet;
import com.mewo.hbmenhanced.ResearchBlock.Research;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/*
Start with a basic Research Block (Tier 1) that can perform simple research tasks.

As you progress, unlock Tier 2, which lets you craft a second Research Block and place it next to the first.

When placed together, these blocks form a Tier 2 multiblock structure that unlocks new research options (like oil).

Later, you unlock Tier 3, adding another Research Block to the structure to create a larger multiblock and access even more advanced research.

The ResearchCore grows over time by adding blocks and upgrading tiers, expanding research capabilities as the game progresses.
 */

public class TileEntityResearchBlock extends TileEntity implements IInventory {

    private Research research;
    public int INVENTORY_SIZE = 3;

    public int currentEnergy = 0;
    public int maxEnergy = 50000;
    public int researchProgress = 0;
    public int maxResearchProgress = 0;
    public boolean isResearching = false;
    private String team;
    public ItemStack[] inventory;



    public int getResearchProgressScaled(int scale) {
        if (maxResearchProgress == 0) return 0;
        return (researchProgress * scale) / maxResearchProgress;
    }

    public TileEntityResearchBlock() {
        inventory = new ItemStack[INVENTORY_SIZE];
        research = new Research();
    }

    public void setTeam(EntityPlayer placer) {
        NBTTagCompound nbt = placer.getEntityData();
        if (nbt != null) {
            this.team = nbt.getString("hbmenhanced:team");
        }
    }

    public String getTeam() {
        return this.team;
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {

            research.Tier2(inventory, 0, 1, 2, this);
        }
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
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
        return "container.researchBlock";
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
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
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
        compound.setString("Team", team);
        compound.setInteger("ResearchProgress", researchProgress);
        compound.setInteger("MaxResearch", maxResearchProgress);
        compound.setBoolean("IsResearching", isResearching);
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
        team = compound.getString("Team");
        researchProgress = compound.getInteger("ResearchProgress");
        maxResearchProgress = compound.getInteger("MaxResearch");
        isResearching = compound.getBoolean("IsResearching");
    }

    public int getBurnTimeScaled(int i) {
        return  1;
    }
}
