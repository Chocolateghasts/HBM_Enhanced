package com.mewo.hbmenhanced.ResearchBlock.Tier1;

import cofh.api.energy.IEnergyContainerItem;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemSelfcharger;
import com.mewo.hbmenhanced.Packets.ResearchTier1Packet;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import java.util.ArrayList;
import java.util.List;

/*
Start with a basic Research Block (Tier 1) that can perform simple research tasks.

As you progress, unlock Tier 2, which lets you craft a second Research Block and place it next to the first.

When placed together, these blocks form a Tier 2 multiblock structure that unlocks new research options (like oil).

Later, you unlock Tier 3, adding another Research Block to the structure to create a larger multiblock and access even more advanced research.

The ResearchCore grows over time by adding blocks and upgrading tiers, expanding research capabilities as the game progresses.
 */

public class TileEntityResearchBlock extends TileEntity implements IInventory {

    private boolean isMainBlock = false;
    private ChunkCoordinates mainBlockPos = null;
    public int tier = 1;
    private List<ChunkCoordinates> connectedBlockPositions = new ArrayList<>();

    private Research research;
    public int INVENTORY_SIZE = 3;

    public int currentEnergy = 0;
    public int maxEnergy = 50000;
    public int currentBurnTime = 0;
    public int researchProgress = 0;
    public int maxResearchProgress = 0;
    public boolean isResearching = false;
    public boolean isBurning = false;
    public boolean isCore = true;
    private String team;
    public ItemStack[] inventory;

    public void setAsMainBlock() {
        this.isMainBlock = true;
        this.mainBlockPos = new ChunkCoordinates(xCoord, yCoord, zCoord);
    }

    public boolean isMainBlock() {
        return isMainBlock;
    }

    public void updateMultiBlock() {
        connectedBlockPositions.clear();

        // If this is not the main block, don't perform structure validation
        if (!isMainBlock) {
            return;
        }

        int count = 1;
        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;

        // Check cardinal directions (NSEW)
        int[][] offsets = {
                { 1,  0,  0},
                {-1,  0,  0},
                { 0,  0,  1},
                { 0,  0, -1}
        };

        // Track blocks by tier
        List<TileEntityResearchBlock> tierOneBlocks = new ArrayList<>();
        List<TileEntityResearchBlock> tierTwoBlocks = new ArrayList<>();

        for (int[] offset : offsets) {
            int dx = x + offset[0];
            int dy = y + offset[1];
            int dz = z + offset[2];

            TileEntity tileEntity = worldObj.getTileEntity(dx, dy, dz);
            if (tileEntity instanceof TileEntityResearchBlock) {
                TileEntityResearchBlock researchBlock = (TileEntityResearchBlock) tileEntity;

                // Skip if it's another main block
                if (researchBlock.isMainBlock()) {
                    continue;
                }

                // Add to appropriate tier list
                if (researchBlock.tier == 1) {
                    tierOneBlocks.add(researchBlock);
                } else if (researchBlock.tier == 2) {
                    tierTwoBlocks.add(researchBlock);
                }

                connectedBlockPositions.add(new ChunkCoordinates(dx, dy, dz));
                count++;
            }
        }

        // Determine new tier based on surrounding blocks
        if (tierTwoBlocks.size() >= 1 && tierOneBlocks.size() >= 1) {
            this.tier = 3;
        } else if (tierOneBlocks.size() >= 1) {
            this.tier = 2;
        } else {
            this.tier = 1;
        }


        // Update connected blocks
        for (ChunkCoordinates pos : connectedBlockPositions) {
            TileEntity te = worldObj.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te instanceof TileEntityResearchBlock) {
                ((TileEntityResearchBlock) te).mainBlockPos = new ChunkCoordinates(x, y, z);
            }
        }
    }


    public int getBurnTimeScaled(int scale) {
        return Math.min(scale, (currentBurnTime * scale) / 200); // Assuming 200 is full burn time
    }

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
            updateMultiBlock();
            switch (tier) {
                case 1:
                    hbmenhanced.network.sendToAllAround(
                            new ResearchTier1Packet(xCoord, yCoord, zCoord, currentBurnTime, researchProgress, maxResearchProgress, isResearching),
                            new NetworkRegistry.TargetPoint(
                                    worldObj.provider.dimensionId,
                                    xCoord, yCoord, zCoord,
                                    64.0D
                            )
                    );
                    research.Tier1(inventory, 0, 1, 2, this);
                    break;
                case 2:
                    System.out.println("Energy: " + currentEnergy);
                    if (currentEnergy < maxEnergy) {
                        ItemStack battery =  inventory[1];
                        if (battery != null) {
                            // TODO: fix
                            if (battery.getItem() instanceof ItemBattery) {
                                ((ItemBattery) battery.getItem()).dischargeBattery(battery, ((ItemBattery) battery.getItem()).getDischargeRate());
                                currentEnergy += (int) ((ItemBattery) battery.getItem()).getDischargeRate();
                            } else if (battery.getItem() instanceof ItemSelfcharger) {
                                currentEnergy += (int) ((ItemSelfcharger) battery.getItem()).getDischargeRate();
                            } else if (battery.getItem() instanceof IEnergyContainerItem) {
                                ((IEnergyContainerItem) battery.getItem()).extractEnergy(battery, 3000, false);
                                currentEnergy += 3000;
                            }
                            research.Tier2(inventory, 0, 1, 2, this);
                            break;
                        }
                    }
            }
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
        compound.setInteger("BurnTime", currentBurnTime);
        compound.setInteger("ResearchProgress", researchProgress);
        compound.setInteger("MaxResearch", maxResearchProgress);
        compound.setBoolean("IsResearching", isResearching);
        compound.setBoolean("IsMainBlock", isMainBlock);
        if (mainBlockPos != null) {
            compound.setInteger("MainX", mainBlockPos.posX);
            compound.setInteger("MainY", mainBlockPos.posY);
            compound.setInteger("MainZ", mainBlockPos.posZ);
        }
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
        currentBurnTime = compound.getInteger("BurnTime");
        researchProgress = compound.getInteger("ResearchProgress");
        maxResearchProgress = compound.getInteger("MaxResearch");
        isResearching = compound.getBoolean("IsResearching");
        isMainBlock = compound.getBoolean("IsMainBlock");
        if (compound.hasKey("MainX")) {
            mainBlockPos = new ChunkCoordinates(
                    compound.getInteger("MainX"),
                    compound.getInteger("MainY"),
                    compound.getInteger("MainZ")
            );
        }
    }
}
