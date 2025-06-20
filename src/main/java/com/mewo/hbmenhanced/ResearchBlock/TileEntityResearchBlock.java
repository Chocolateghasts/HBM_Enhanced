package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import java.util.ArrayList;
import java.util.List;

public class TileEntityResearchBlock extends TileEntity implements IInventory {
    public int tier = 1;
    private List<ChunkCoordinates> connectedBlockPositions = new ArrayList<ChunkCoordinates>();

    private Research research;
    public int INVENTORY_SIZE = 3;

    public int currentBurnTime = 0;
    public int researchProgress = 0;
    public int maxResearchProgress = 0;
    public boolean isResearching = false;
    private String team;

    public ItemStack[] inventory;

    public TileEntityResearchBlock() {
        inventory = new ItemStack[INVENTORY_SIZE];
        research = new Research();
    }

    public void setTeam(EntityPlayer placer) {
        NBTTagCompound nbt = placer.getEntityData();
        if (nbt != null) {
            String team = nbt.getString("hbmenhanced:team");
            this.team = team;
        }
    }

    public String getTeam() {
        return this.team;
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            updateMultiBlock();
            switch (tier) {
                case 1:
                    research.Tier1(inventory, 0, 1, this);
            }
        }
    }

    public void updateMultiBlock() {
        connectedBlockPositions.clear();

        int count = 1; // self
        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;

        // 4 cardinal directions (NSEW)
        int[][] offsets = {
                { 1,  0,  0},
                {-1,  0,  0},
                { 0,  0,  1},
                { 0,  0, -1}
        };

        for (int[] offset : offsets) {
            int dx = x + offset[0];
            int dy = y + offset[1];
            int dz = z + offset[2];

            TileEntity tileEntity = worldObj.getTileEntity(dx, dy, dz);
            if (tileEntity instanceof TileEntityResearchBlock) {
                connectedBlockPositions.add(new ChunkCoordinates(dx, dy, dz));
                count++;
            }
        }
        this.tier = Math.min(count, 3);
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
        compound.setInteger("BurnTime", currentBurnTime);
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
        currentBurnTime = compound.getInteger("BurnTime");
        researchProgress = compound.getInteger("ResearchProgress");
        maxResearchProgress = compound.getInteger("MaxResearch");
        isResearching = compound.getBoolean("IsResearching");
    }
}
