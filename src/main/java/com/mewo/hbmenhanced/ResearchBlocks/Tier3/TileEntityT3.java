package com.mewo.hbmenhanced.ResearchBlocks.Tier3;

import api.hbm.energymk2.IEnergyConductorMK2;
import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.energymk2.Nodespace;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.Compat;
import com.hbm.util.fauxpointtwelve.BlockPos;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.*;
import com.mewo.hbmenhanced.ResearchBlock.Research;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.TileEntityResearchController;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TileEntityT3 extends TileEntity implements IInventory, IEnergyReceiverMK2, IEnergyConductorMK2, IResearchProvider {
    public String team;
    public ItemStack[] inventory;
    private Research research;

    public long currentEnergy;
    public long maxEnergy = 50000;
    private boolean isSubscribed;
    public DirPos dirPos;
    public AbstractNetwork<?> network;
    public NetworkNodeType type;

    public boolean isResearching;
    public int researchProgress;
    public int maxResearchProgress;

    public int MAIN_SLOT = 0;
    public int OUTPUT_SLOT = 2;
    public int BATTERY_SLOT = 1;

    private int subscribeTickCounter;

    private final int INV_SIZE = 3;

    public TileEntityResearchController core;

    public TileEntityT3() {
        inventory = new ItemStack[INV_SIZE];
        research = new Research();
    }

    public void tryAllSubscriptions() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int nx = xCoord + dir.offsetX;
            int ny = yCoord + dir.offsetY;
            int nz = zCoord + dir.offsetZ;

            //System.out.println("Trying to subscribe on side: " + dir);
            trySubscribe(worldObj, nx, ny, nz, dir);
        }
    }


    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            research.Tier3(this);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
            subscribeTickCounter++;
//            setPower(currentEnergy-10);
            if (!isSubscribed) {
                tryAllSubscriptions();
            }
            if (subscribeTickCounter >= 10) {
                subscribeTickCounter = 0;
                tryAllSubscriptions();
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
        return "Research Block MK3";
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
        NBTTagCompound items = new NBTTagCompound();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                items.setTag("Slot" + i, item);
            }
        }
        compound.setTag("Items", items);
        compound.setLong("EnergyStored", currentEnergy);
        NBTTagCompound researchData = new NBTTagCompound();
        researchData.setBoolean("isResearching", isResearching);
        researchData.setInteger("researchProgress", researchProgress);
        researchData.setInteger("maxResearchProgress", maxResearchProgress);
        compound.setTag("researchData", researchData);
        compound.setString("Team", team);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
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
        currentEnergy = compound.getLong("EnergyStored");
//        compound.setTag("Items", items);
//        compound.setLong("EnergyStored", currentEnergy);
        NBTTagCompound researchData = compound.getCompoundTag("researchData");
        isResearching =  researchData.getBoolean("isResearching");
        researchProgress = researchData.getInteger("researchProgress");
        maxResearchProgress = researchData.getInteger("maxResearchProgress");
        team = compound.getString("Team");
    }

    @Override
    public long getPower() {
        return currentEnergy;
    }

    @Override
    public void setPower(long power) {
        currentEnergy = power;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public long getMaxPower() {
        return maxEnergy;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
    @Override
    public void trySubscribe(World world, int x, int y, int z, ForgeDirection dir) {
        TileEntity te = Compat.getTileStandard(world, x, y, z);
        boolean red = false;

        if(te instanceof IEnergyConductorMK2) {
            IEnergyConductorMK2 con = (IEnergyConductorMK2) te;
            if(!con.canConnect(dir.getOpposite())) return;

            Nodespace.PowerNode node = Nodespace.getNode(world, x, y, z);
            if(node != null && node.net != null) {
                node.net.addReceiver(this);
                isSubscribed = true;
                red = true;
            }
        }

        if(particleDebug) {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("type", "network");
            data.setString("mode", "power");
            double posX = x + 0.5 + dir.offsetX * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            double posY = y + 0.5 + dir.offsetY * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            double posZ = z + 0.5 + dir.offsetZ * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            data.setDouble("mX", -dir.offsetX * (red ? 0.025 : 0.1));
            data.setDouble("mY", -dir.offsetY * (red ? 0.025 : 0.1));
            data.setDouble("mZ", -dir.offsetZ * (red ? 0.025 : 0.1));
            PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, posX, posY, posZ), new NetworkRegistry.TargetPoint(world.provider.dimensionId, posX, posY, posZ, 25));
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