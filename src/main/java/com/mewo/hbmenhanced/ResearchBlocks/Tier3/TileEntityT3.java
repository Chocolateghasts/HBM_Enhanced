package com.mewo.hbmenhanced.ResearchBlocks.Tier3;

import api.hbm.energymk2.IEnergyConductorMK2;
import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.energymk2.Nodespace;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.Compat;
import com.mewo.hbmenhanced.ResearchBlock.Research;
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

import java.util.Arrays;

public class TileEntityT3 extends TileEntity implements IInventory, IEnergyReceiverMK2, IEnergyConductorMK2 {
    public String team;
    public ItemStack[] inventory;
    private Research research;

    public long currentEnergy;
    public long maxEnergy = 50000;
    private boolean isSubscribed;

    private int subscribeTickCounter;

    private final int INV_SIZE = 3;

    public TileEntityT3() {
        inventory = new ItemStack[INV_SIZE];
    }

    public void tryAllSubscriptions() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int nx = xCoord + dir.offsetX;
            int ny = yCoord + dir.offsetY;
            int nz = zCoord + dir.offsetZ;

            System.out.println("Trying to subscribe on side: " + dir);
            trySubscribe(worldObj, nx, ny, nz, dir);
        }
    }


    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            subscribeTickCounter++;
            setPower(currentEnergy-10);
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
            System.out.println(node);
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
}