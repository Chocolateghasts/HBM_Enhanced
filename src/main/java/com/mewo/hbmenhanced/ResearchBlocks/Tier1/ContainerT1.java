package com.mewo.hbmenhanced.ResearchBlocks.Tier1;

import cofh.api.energy.IEnergyContainerItem;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemSelfcharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerT1 extends Container {

    private TileEntityT1 tileEntity;


    public ContainerT1(InventoryPlayer inventory, TileEntityT1 te) {
        this.tileEntity = te;
        addSlotToContainer(new Slot(te, 0, 9, 28));
        addSlotToContainer(new Slot(te, 1, 9, 62){
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return TileEntityFurnace.getItemBurnTime(itemStack) > 0;
//                    if (itemStack.getItem() instanceof IEnergyContainerItem || itemStack.getItem() instanceof ItemBattery || itemStack.getItem() instanceof ItemSelfcharger) {
//                        return true;
//                    }
            }
        });
        addSlotToContainer(new Slot(te, 2, 55, 28));

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

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}
