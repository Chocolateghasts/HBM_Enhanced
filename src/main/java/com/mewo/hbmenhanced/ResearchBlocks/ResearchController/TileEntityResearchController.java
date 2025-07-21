package com.mewo.hbmenhanced.ResearchBlocks.ResearchController;

import api.hbm.energymk2.IEnergyReceiverMK2;
import com.hbm.util.fauxpointtwelve.BlockPos;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Map;

public class TileEntityResearchController extends TileEntity implements IInventory, IEnergyReceiverMK2 {

    // MultiBlock stuff
    Map<Integer, TileEntity> connectedTiers;
    Map<TileEntity, BlockPos> connectedPos;

    //Constants
    private final int INV_SIZE = 3;

    // Properties
    public ItemStack[] inventory;

    public TileEntityResearchController() {
        inventory = new ItemStack[INV_SIZE];
        connectedTiers = new HashMap<>();
        connectedPos = new HashMap<>();
    }

    // Custom Methods
    private void tryAllSubscriptions() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int nx = xCoord + dir.offsetX;
            int ny = yCoord + dir.offsetY;
            int nz = zCoord + dir.offsetZ;

            //System.out.println("Trying to subscribe on side: " + dir);
            trySubscribe(worldObj, nx, ny, nz, dir);
        }
    }

    // MultiBlock Methods

    private void setCore(TileEntity te, TileEntityResearchController controller) {
        if (te instanceof TileEntityT1) {
            ((TileEntityT1) te).core = controller;
            System.out.println("Set core for T1 block at " + te.xCoord + ", " + te.yCoord + ", " + te.zCoord);
        } else if (te instanceof TileEntityT2) {
            ((TileEntityT2) te).core = controller;
            System.out.println("Set core for T2 block at " + te.xCoord + ", " + te.yCoord + ", " + te.zCoord);
        } else if (te instanceof TileEntityT3) {
            ((TileEntityT3) te).core = controller;
            System.out.println("Set core for T3 block at " + te.xCoord + ", " + te.yCoord + ", " + te.zCoord);
        }
    }

    public void addConnection(TileEntity te) {
        if (!isResearchBlock(te)) {
            System.out.println("Attempted to add non-research block at " + te.xCoord + ", " + te.yCoord + ", " + te.zCoord);
            return;
        }
        int blockTier = getTierOfBlock(te);
        if (connectedTiers.containsKey(blockTier)) {
            System.out.println("Tier " + blockTier + " already connected, skipping.");
            return;
        }
        connectedTiers.put(blockTier, te);
        connectedPos.put(te, new BlockPos(te));
        setCore(te, this);
        System.out.println("Connected tier " + blockTier + " at " + te.xCoord + ", " + te.yCoord + ", " + te.zCoord);
    }

    public void removeConnection(TileEntity te) {
        if (!isResearchBlock(te)) {
            System.out.println("Attempted to remove non-research block.");
            return;
        }
        int blockTier = getTierOfBlock(te);
        if (!connectedTiers.containsKey(blockTier)) return;
        if (!connectedTiers.containsValue(te)) return;

        connectedTiers.remove(blockTier);
        connectedPos.remove(te);
        System.out.println("Removed tier " + blockTier + " connection.");
    }

    public boolean isResearchBlock(TileEntity te) {
        return te instanceof TileEntityT1 || te instanceof TileEntityT2 || te instanceof TileEntityT3;
    }

    public int getTierOfBlock(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityT1) {
            return 1;
        } else if (tileEntity instanceof TileEntityT2) {
            return 2;
        } else if (tileEntity instanceof TileEntityT3) {
            return 3;
        } else {
            return 0;
        }
    }

    public boolean canResearch(TileEntity tileEntity) {
        if (!isResearchBlock(tileEntity)) {
            System.out.println("Block is not a valid research block.");
            return false;
        }

        int tier = getTierOfBlock(tileEntity);

        if (connectedTiers.get(tier) != tileEntity) {
            System.out.println("Block at tier " + tier + " is not connected.");
            return false;
        }

        if (tier == 1) {
            System.out.println("Tier 1 block can research (no prerequisites).");
            return true;
        }

        for (int i = 1; i < tier; i++) {
            if (!connectedTiers.containsKey(i) || connectedTiers.get(i) == null) {
                System.out.println("Missing required tier " + i + " connection.");
                return false;
            }
        }

        System.out.println("All required tiers connected. Tier " + tier + " can research.");
        return true;
    }


    // Important Methods
    @Override
    public void updateEntity() {
        super.updateEntity();
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

    // NBT Handling
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

    // Getter/Setters
    @Override
    public long getPower() {
        return 0;
    }

    @Override
    public void setPower(long l) {

    }

    @Override
    public long getMaxPower() {
        return 0;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    // Constant Methods
    @Override
    public String getInventoryName() {
        return "Research Controller";
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
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
