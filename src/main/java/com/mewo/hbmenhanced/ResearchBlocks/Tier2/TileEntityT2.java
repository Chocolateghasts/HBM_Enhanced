package com.mewo.hbmenhanced.ResearchBlocks.Tier2;

import api.hbm.fluid.IFluidStandardReceiver;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.tileentity.IFluidCopiable;
import com.mewo.hbmenhanced.ResearchBlock.Research;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityT2 extends TileEntity implements IInventory, IFluidStandardReceiver {

    // Constants
    public final int INV_SIZE = 4;
    public final int MAIN_SLOT = 0;
    public final int SECOND_SLOT = 1;
    public final int OUTPUT_SLOT = 2;
    public final int FLUID_SLOT = 3;

    // Properties ?
    public Research research;
    public ItemStack[] inventory;
    public String team;

    // Variables
    public int researchProgress;
    public int maxResearchProgress;
    public boolean isResearching;
    public int currentBurnTime;
    public int totalBurnTime;
    public boolean isBurning;
    public FluidTank tank;
    private int tickCounter;
    private int tickCounterClient;

    public TileEntityT2() {
        inventory = new ItemStack[INV_SIZE];
        research = new Research();
        tank = new FluidTank(Fluids.WATER, 16000);
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            tickCounterClient++;
            if (tickCounterClient >= 20) {
                tickCounterClient = 0;
//                System.out.println("[Client] Fluid Stored: " + tank.getFill());
            }
        }
        if (!worldObj.isRemote) {
            research.Tier2(this);
            tickCounter++;

            if (tickCounter % 5 == 0) {
                markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
            if (tickCounter >= 20) {
                tickCounter = 0;
//                System.out.println("[SERVER] Fluid Stored: " + tank.getFill());
                subscribeToAllAround(tank.getTankType(), this);
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return INV_SIZE;
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
            } else {
                itemStack = inventory[slot].splitStack(count);
                if (inventory[slot].stackSize == 0) {
                    inventory[slot] = null;
                }
            }
            markDirty();
            return itemStack;
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
        return "Research Block MK2";
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
    public boolean isItemValidForSlot(int slot, ItemStack p_94041_2_) {
        return false;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        tank.writeToNBT(compound, "tank");
        NBTTagCompound items = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                items.setTag("Slot" + i, item);
            }
        }
        compound.setTag("Items", items);
        compound.setInteger("ResearchProgress", researchProgress);
        compound.setInteger("MaxResearchProgress", maxResearchProgress);
        compound.setBoolean("IsResearching", isResearching);
        compound.setInteger("CurrentBurnTime", currentBurnTime);
        compound.setInteger("TotalBurnTime", totalBurnTime);
        compound.setBoolean("IsBurning", isBurning);
        compound.setString("Team", team);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tank.readFromNBT(compound, "tank");
        NBTTagCompound items = compound.hasKey("Items") ? compound.getCompoundTag("Items") : new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (items.hasKey("Slot" + i)) {
                NBTTagCompound item = items.getCompoundTag("Slot" + i);
                ItemStack loaded = ItemStack.loadItemStackFromNBT(item);
                inventory[i] = loaded != null ? loaded : null;
            } else {
                inventory[i] = null;
            }
        }
        researchProgress = compound.getInteger("ResearchProgress");
        maxResearchProgress = compound.getInteger("MaxResearchProgress");
        isResearching = compound.getBoolean("IsResearching");
        currentBurnTime = compound.getInteger("CurrentBurnTime");
        totalBurnTime = compound.getInteger("TotalBurnTime");
        isBurning = compound.getBoolean("IsBurning");
        team = compound.getString("Team");
    }
    @Override
    public FluidTank[] getAllTanks() {
        return new FluidTank[] { tank };
    }

    @Override
    public FluidTank[] getReceivingTanks() {
        return new FluidTank[] { tank };
    }

    @Override
    public boolean canConnect(FluidType type, ForgeDirection dir) {
        return true;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public long transferFluid(FluidType type, int pressure, long amount) {
        int tanks = 0;
        for(FluidTank tank : getReceivingTanks()) {
            if(tank.getTankType() == type && tank.getPressure() == pressure) tanks++;
        }
        if(tanks > 1) {
            //System.out.println("Attempting transfer");
            int firstRound = (int) Math.floor((double) amount / (double) tanks);
            for(FluidTank tank : getReceivingTanks()) {
                //System.out.println("Checking for tank " + tank.getTankType());
                if(tank.getTankType() == type && tank.getPressure() == pressure) {
                    //System.out.println("Pressure is correct!");
                    int toAdd = Math.min(firstRound, tank.getMaxFill() - tank.getFill());
                    tank.setFill(tank.getFill() + toAdd);
                    amount -= toAdd;
                }
            }
        }
        if(amount > 0) for(FluidTank tank : getReceivingTanks()) {
            //System.out.println("[Bottom method]: Checking for tank " + tank.getTankType());
            if(tank.getTankType() == type && tank.getPressure() == pressure) {
                //System.out.println("[Bottom method]: Pressure and type is correct!");
                int toAdd = (int) Math.min(amount, tank.getMaxFill() - tank.getFill());
                tank.setFill(tank.getFill() + toAdd);
                amount -= toAdd;
            }
        }
        //System.out.println("Amount: " + amount);
        return amount;
    }

}