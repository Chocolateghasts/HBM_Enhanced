package com.mewo.hbmenhanced.ResearchBlocks.Tier2;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.items.machine.ItemFluidIdentifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerT2 extends Container {
    public TileEntityT2 tileEntity;

    public ContainerT2(InventoryPlayer inventory, TileEntityT2 te) {
        this.tileEntity = te;
        addSlotToContainer(new Slot(te, 0, 10, 29));
        addSlotToContainer(new Slot(te, 1, 10, 63){
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return TileEntityFurnace.getItemBurnTime(itemStack) > 0;
//                    if (itemStack.getItem() instanceof IEnergyContainerItem || itemStack.getItem() instanceof ItemBattery || itemStack.getItem() instanceof ItemSelfcharger) {
//                        return true;
//                    }
            }
        });
        addSlotToContainer(new Slot(te, 2, 56, 29));
        addSlotToContainer(new Slot(te, 3, 103, 63) {
            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                this.inventory.markDirty();
                if (tileEntity.inventory[3] != null && tileEntity.inventory[3].getItem() instanceof ItemFluidIdentifier) {
                    System.out.println("FounD FLuiD iDtentifieR");
                    if (ItemFluidIdentifier.getType(tileEntity.inventory[3]).hasTrait(FT_Flammable.class)) {
                        tileEntity.tank.setTankType(ItemFluidIdentifier.getType(tileEntity.inventory[3]));
                    }
                }
            }
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
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlotIndex) {
        ItemStack transferredStack = null;
        Slot clickedSlot = (Slot)this.inventorySlots.get(clickedSlotIndex);
        if (clickedSlot != null && clickedSlot.getHasStack()) {
            ItemStack originalStack = clickedSlot.getStack();
            transferredStack = originalStack.copy();
            if (clickedSlotIndex == 2) {
                if (!this.mergeItemStack(originalStack, 3, 39, true)) {
                    return null;
                }

                clickedSlot.onSlotChange(originalStack, transferredStack);
            }
            else if (clickedSlotIndex >= 3 && clickedSlotIndex < 39) {
                if (TileEntityFurnace.isItemFuel(originalStack)) {
                    if (!this.mergeItemStack(originalStack, 1, 2, false)) {
                        return null;
                    }
                }
                else if (tileEntity.isItemValidForSlot(0, originalStack)) {
                    if (!this.mergeItemStack(originalStack, 0, 1, false)) {
                        return null;
                    }
                }
                else if (clickedSlotIndex < 30) {
                    if (!this.mergeItemStack(originalStack, 30, 39, false)) {
                        return null;
                    }
                } else {
                    if (!this.mergeItemStack(originalStack, 3, 30, false)) {
                        return null;
                    }
                }
            }
            else {
                if (!this.mergeItemStack(originalStack, 3, 39, false)) {
                    return null;
                }
            }
            if (originalStack.stackSize == 0) {
                clickedSlot.putStack(null);
            } else {
                clickedSlot.onSlotChanged();
            }
            if (originalStack.stackSize == transferredStack.stackSize) {
                return null;
            }
            clickedSlot.onPickupFromSlot(player, originalStack);
        }
        return transferredStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}
