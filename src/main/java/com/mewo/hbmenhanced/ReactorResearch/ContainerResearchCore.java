package com.mewo.hbmenhanced.ReactorResearch;

import cofh.api.energy.IEnergyContainerItem;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemSelfcharger;
import com.hbm.tileentity.machine.TileEntityReactorResearch;
import com.mewo.hbmenhanced.items.ItemLink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

public class ContainerResearchCore extends Container {
    private TileEntityResearchCore tileEntity;
    @Override
    public boolean enchantItem(EntityPlayer player, int buttonId) {
        if (buttonId == 0) { // BUTTON_ID_EXPLODE
            // This runs on the server side
            tileEntity.getReactor().heat = 999999;
            tileEntity.getReactor().water = 0;
            tileEntity.getReactor().level = 999;
            tileEntity.getReactor().updateEntity();
            try {
                Method explodeMethod = TileEntityReactorResearch.class.getDeclaredMethod("explode");
                explodeMethod.setAccessible(true);
                explodeMethod.invoke(tileEntity.getReactor());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public ContainerResearchCore(InventoryPlayer inventory, TileEntityResearchCore te) {
        this.tileEntity = te;

        addSlotToContainer(new Slot(te, 0,12, 10)
        {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem() instanceof ItemLink;
            }
            @Override
            public void onSlotChanged() {super.onSlotChanged();}
        });
        addSlotToContainer(new Slot(te, 1,12, 30));
        addSlotToContainer(new Slot(te, 2,134, 37) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                if (itemStack.getItem() instanceof IEnergyContainerItem || itemStack.getItem() instanceof ItemBattery || itemStack.getItem() instanceof ItemSelfcharger) {
                    return true;
                }
                return false;
            }
            @Override
            public void onSlotChanged() {super.onSlotChanged();}
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex < tileEntity.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, tileEntity.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
