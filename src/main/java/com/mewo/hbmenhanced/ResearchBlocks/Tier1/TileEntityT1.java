package com.mewo.hbmenhanced.ResearchBlocks.Tier1;

import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.*;
import com.mewo.hbmenhanced.ResearchBlock.Research;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.TileEntityResearchController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityT1 extends TileEntity implements IInventory, IResearchProvider {

    // Constants
    private final int INV_SIZE = 3;
    public final int MAIN_SLOT = 0;
    public final int FUEL_SLOT = 1;
    public final int OUTPUT_SLOT = 2;

    // Properties ?
    public ItemStack[] inventory;
    public Research research;
    public String team;
    public DirPos dirPos;
    public AbstractNetwork<?> network;
    public NetworkNodeType type;

    // Variables
    public int totalBurnTime;
    public boolean isResearching;
    public int researchProgress;
    public int currentBurnTime;
    public int maxResearchProgress;
    public boolean isBurning;

    private int tickCounter = 0;

    public TileEntityResearchController core;

    public TileEntityT1() {
        inventory = new ItemStack[INV_SIZE];
        research = new Research();
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            research.Tier1(this);
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        return "Research Block MK1";
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
        switch (slot) {
            case 0: return true;
            case 1: return TileEntityFurnace.getItemBurnTime(stack) > 0;
            case 2: return true;
            default: return true;
        }

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
//        // Variables
//        public boolean isResearching;
//        public int researchProgress;
//        public int currentBurnTime;
//        public int maxResearchProgress;
//        public boolean isBurning;
//
//        private int tickCounter = 0;

        NBTTagCompound researchData = new NBTTagCompound();
        researchData.setInteger("totalBurnTime", totalBurnTime);
        researchData.setBoolean("isResearching", isResearching);
        researchData.setInteger("researchProgress", researchProgress);
        researchData.setInteger("currentBurnTime", currentBurnTime);
        researchData.setInteger("maxResearchProgress", maxResearchProgress);
        researchData.setBoolean("isBurning", isBurning);
        // Is the tickcounter needed in the NBT save/load?
        researchData.setInteger("tickCounter", tickCounter);



        NBTTagCompound items = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                items.setTag("Slot" + i, item);
            }
        }
        compound.setTag("Items", items);
        compound.setTag("researchData", researchData);
        compound.setString("Team", team);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagCompound researchData = compound.hasKey("researchData") ? compound.getCompoundTag("researchData") : new NBTTagCompound();
        totalBurnTime = researchData.getInteger("totalBurnTime");
        isResearching = researchData.getBoolean("isResearching");
        researchProgress = researchData.getInteger("researchProgress");
        currentBurnTime = researchData.getInteger("currentBurnTime");
        maxResearchProgress = researchData.getInteger("maxResearchProgress");
        isBurning = researchData.getBoolean("isBurning");
        team = compound.getString("Team");
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
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(xCoord, yCoord, zCoord);
    }

    @Override
    public DirPos getDirPos() {
        if (dirPos == null) {
            return new DirPos(getPos().getX(), getPos().getY(), getPos().getZ(), ForgeDirection.UNKNOWN);
        }
        return dirPos;
    }

    public AbstractNetwork<?> getNetwork() {
        if (network == null && worldObj != null) {
            network = (AbstractNetwork<?>) ResearchNetworkManager.getNetwork(worldObj, getType());
        }
        return network;
    }

    @Override
    public NetworkNodeType getType() {
        if (this.type == null) {
            this.type = NetworkNodeType.CONTROLLER;
        }
        return this.type;
    }

    @Override
    public List<BlockPos> getNeighbors() {
        List<BlockPos> pos = new ArrayList<>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = worldObj.getTileEntity(
                    xCoord + dir.offsetX,
                    yCoord + dir.offsetY,
                    zCoord + dir.offsetZ);
            if (te instanceof IConnectableNode && ((IConnectableNode) te).getType() == this.type) {
                pos.add(new BlockPos(te));
            }
        }
        return pos;
    }

    @Override
    public void setNetwork(AbstractNetwork<?> network) {
        this.network = network;
    }

    @Override
    public void setDirPos(DirPos dirPos) {
        this.dirPos = dirPos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate() {
        super.validate();
        if (!worldObj.isRemote) {
            AbstractNetwork<IConnectableNode> net = (AbstractNetwork<IConnectableNode>) ResearchNetworkManager.getNetwork(worldObj, getType());
            setNetwork(net);
            net.add(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate() {
        super.invalidate();
        if (!worldObj.isRemote && getNetwork() != null) {
            AbstractNetwork<IConnectableNode> net = (AbstractNetwork<IConnectableNode>) ResearchNetworkManager.getNetwork(worldObj, getType());
            net.remove(this);
        }
    }

    @Override
    public void setType(NetworkNodeType type) {

    }
}
