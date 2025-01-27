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
        return null;
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {

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
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    public static boolean isResearchItem(ItemStack itemStack) {
        //if (ItemStack )
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if (i == 1) {return false;}
        //if (1 == 0 && itemStack == isResearchItem()) {}
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int i) {
        if (i == 5) { return slot_input;}
        if (i == 4) { return slot_output;}
        return new int[]{};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, int j) {
        return i == 0 && this.isItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, int j) {
        return i == 1;
    }
}
