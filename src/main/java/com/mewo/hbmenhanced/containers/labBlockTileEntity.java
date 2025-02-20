package com.mewo.hbmenhanced.containers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mewo.hbmenhanced.hbmenhanced.researchPoint;

public class labBlockTileEntity extends TileEntity implements ISidedInventory {

    private ItemStack[] slots = new ItemStack [2];

    private static final int[] slot_input = new int[]{0};
    private static final int[] slot_output = new int[]{1};
    public int currentItemResearchTime;
    public int researchTime;
    public int researchSpeed = 5;

    private List<ItemStack> researchItems = new ArrayList<>();

    public void loadResearchItems() {
        try {
            FileReader reader = new FileReader("config/hbmenhanced/research_items.json");
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray itemsArray = jsonObject.getAsJsonArray("researchItems");
            for (int i = 0; i < itemsArray.size(); i++) {
                String itemName = itemsArray.get(i).getAsString();
                Item item = (Item) Item.itemRegistry.getObject(new ResourceLocation(itemName));
                if (item != null) {
                    researchItems.add(new ItemStack(item));
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSizeInventory() {
        return this.slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.slots[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        if (this.slots[i] != null) {
            ItemStack itemstack;

            if (this.slots[i].stackSize <= count) {
                itemstack = this.slots[i];
                this.slots[i] = null;
                return itemstack; // Return the full stack if it's smaller than `count`
            } else {
                itemstack = this.slots[i].splitStack(count);
                if (this.slots[i].stackSize == 0) {
                    this.slots[i] = null;
                }
                return itemstack; // Return the split part
            }
        }
        return null;
    }


    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        if(this.slots[i] != null) {
            ItemStack itemStack = this.slots[i];
            this.slots[i] = null;
            return itemStack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        this.slots[i] = itemStack;
        if(itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return "";
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
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord +0.5D, (double)this.yCoord +0.5D, (double)this.zCoord +0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    public static boolean isResearchItem(ItemStack itemStack) {
        //if (ItemStack )
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        System.out.println("Checking slot validity: Slot = " + i + ", Item = " + itemStack.getDisplayName());
        return i == 0 && isResearchItem(itemStack); // Only allow inserting into input slot
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int i) {
        if (i == 5) { return slot_input; } // Only allow input slot
        return new int[]{}; // Prevent external access to slot 1
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, int j) {
        return i == 0 && this.isItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, int j) {
        return i == 1;
    }
    public int getResearchTimeRemainingScaled(int i) {
        if (this.currentItemResearchTime == 0) {
            this.currentItemResearchTime = this.researchSpeed;
        }
        return this.researchTime * i / this.currentItemResearchTime;
    }
    public int getResearchProgressScale(int i) {
        return this.researchTime * i /this.researchSpeed;
    }
}