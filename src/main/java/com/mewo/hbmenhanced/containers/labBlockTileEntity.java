package com.mewo.hbmenhanced.containers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mewo.hbmenhanced.getRpValue;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
/*
local component = require("component")
local rp = component.RPComponent

local players = rp.getPlayerRP() -- Get RP data

for name, points in pairs(players) do
    print(name .. " has " .. points .. " RP")
end
 */
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.mewo.hbmenhanced.hbmenhanced.researchPoint;
import static com.mewo.hbmenhanced.hbmenhanced.tabhbmenhanced;

public class labBlockTileEntity extends TileEntity implements ISidedInventory {

    private ItemStack[] slots = new ItemStack [2];

    private static final int[] slot_input = new int[]{0};
    private static final int[] slot_output = new int[]{1};
    public int currentItemResearchTime;
    public int researchTime = 120;
    public int researchSpeed = 5;
    public boolean isResearching = false;
    private boolean isProcessing = false;
    public int timer = 0;

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
                return itemstack;
            } else {
                itemstack = this.slots[i].splitStack(count);
                if (this.slots[i].stackSize == 0) {
                    this.slots[i] = null;
                }
                return itemstack;
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
        if (itemStack == null || itemStack.getItem() == null) return false;
        String itemName = itemStack.getDisplayName().toLowerCase();
        return getRpValue.getRpMap().containsKey(itemName);
    }


    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == null) return false;

        // Use getUnlocalizedName() to get a unique name for the item
        String itemName = itemStack.getItem().getUnlocalizedName().toLowerCase();

        // Log the name to ensure it's what you expect
        System.out.println("Checking slot validity: Slot = " + i + ", Item = " + itemName);

        // Check if the item name exists in your RP map
        boolean isValid = getRpValue.getRpMap().containsKey(itemName);

        System.out.println("Item valid: " + isValid);
        System.out.println("isvalid");

        return i == 0 && isValid;

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
    public int getResearchTimeRemainingScaled(int scale) {
        // Changed to use timer instead of currentItemResearchTime
        if (this.researchTime == 0) return 0;
        return (this.timer * scale) / this.researchTime;
    }

    public int getResearchProgressScale(int i) {
        return this.researchTime * i / this.researchSpeed;
    }
    @Override
    public void invalidate() {
        super.invalidate();
        // Stop any ongoing research when the tile entity is invalidated
        this.isResearching = false;
        this.timer = 0;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        // Clean up when chunk unloads
        this.isResearching = false;
        this.timer = 0;
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            boolean wasResearching = isResearching;

            // Check if we should start researching
            if (slots[0] != null && isResearchItem(slots[0]) && slots[1] == null) {
                isResearching = true;
            } else {
                isResearching = false;
            }

            // Do research if active
            if (isResearching && slots[0] != null) {
                timer++;

                // Add this line for debugging
                System.out.println("Research Progress: " + timer + " / " + researchTime);

                if (timer >= researchTime) {
                    // ... existing completion code ...
                    timer = 0;
                }

                // Mark dirty to ensure updates are sent to client
                this.markDirty();
            } else if (!isResearching && timer > 0) {
                timer = 0;
                this.markDirty();
            }
        }
    }

}