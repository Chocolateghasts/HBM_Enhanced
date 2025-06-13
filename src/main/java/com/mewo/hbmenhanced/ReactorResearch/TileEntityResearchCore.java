package com.mewo.hbmenhanced.ReactorResearch;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import com.hbm.tileentity.machine.TileEntityReactorResearch;
import com.mewo.hbmenhanced.getRpValue;
import com.mewo.hbmenhanced.items.ItemLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityResearchCore extends TileEntity implements IInventory, IEnergyHandler {
    public static final int INVENTORY_SIZE = 3;

    private int maxEnergy = 5000;
    private int currentEnergy;

    protected ItemStack[] inventory;
    protected String teamName;

    private int tickCounter = 0;


    public TileEntityResearchCore() {
        inventory = new ItemStack[INVENTORY_SIZE];
    }

    public void setTeam(EntityPlayer player) {
        NBTTagCompound nbt = player.getEntityData();
        String team = nbt.getString("hbmenhanced:team");
        if (team != null) {
            teamName = team;
        }
    }

    public String getTeam() {
        if (teamName != null) {
            return teamName;
        }
        System.out.println("No team found");
        return null;
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            tickCounter++;
            if (tickCounter == 20) {
                tickCounter = 0;
                TileEntityReactorResearch reactor = getReactor();
                if (reactor != null) {
                    if (currentEnergy > 0) {
                        currentEnergy -= 100;
                        System.out.println("Storing " + currentEnergy + "/" + maxEnergy);
                        analyseReactor(reactor);
                        markDirty();
                    }
                }
            }
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxEnergy - currentEnergy, maxReceive);
        if (from == ForgeDirection.EAST) {
            if (!simulate) {
                System.out.println("Received " + energyReceived);
                currentEnergy += energyReceived;
                markDirty();
            }
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return currentEnergy;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return maxEnergy;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        if (from == ForgeDirection.EAST) {
            return true;
        }
        return false;
    }

    public TileEntityReactorResearch getReactor() {
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
        int flux = reactor.totalFlux;
        int water = reactor.getWater();
        int heat = (int) Math.round((reactor.heat) * 0.00002 * 980 + 20);
        int maxHeat = (int) Math.round((reactor.maxHeat) * 0.00002 * 980 + 20);
        boolean isStable = true;

        float heatRatio = (float) heat / maxHeat;

        if (heatRatio > 0.4 && heatRatio < 0.6) {
            getRpValue.addResearchPoints(teamName, getRpValue.researchType.NUCLEAR, 2);
            System.out.println("Added Points to team " + teamName);
            isStable = true;
        } else if (heatRatio > 0.6 && heatRatio < 0.9) {
            isStable = true;
            getRpValue.addResearchPoints(teamName, getRpValue.researchType.NUCLEAR, 4);
            System.out.println("Added Points to team " + teamName);
            getRpValue.addResearchPoints(teamName, getRpValue.researchType.CHEMICAL, 2);
            System.out.println("Added Points to team " + teamName);
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
        NBTTagCompound data = new NBTTagCompound();
        NBTTagCompound items = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                items.setTag("Slot" + i, item);
            }
        }
        if (teamName != null) {
            data.setString("team", teamName);
        }

        compound.setTag("hbmenhanced:data", data);
        compound.setTag("Items", items);
        compound.setInteger("Energy", currentEnergy);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound data = compound.getCompoundTag("hbmenhanced:data");
        NBTTagCompound items = compound.getCompoundTag("Items");
        for (int i = 0; i < inventory.length; i++) {
            if (items.hasKey("Slot" + i)) {
                NBTTagCompound item = items.getCompoundTag("Slot" + i);
                inventory[i] = ItemStack.loadItemStackFromNBT(item);
            }
        }
        String name = data.getString("team");
        if (name != null) {
            teamName = name;
        }
        currentEnergy = compound.getInteger("Energy");
    }
}