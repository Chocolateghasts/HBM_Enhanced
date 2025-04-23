package com.mewo.hbmenhanced.containers;

import com.mewo.hbmenhanced.SlotResearchItem;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.inventory.Container;

public class labBlockContainer extends Container {

    private labBlockTileEntity labBlock;
    public static boolean isActive = true;
    public int lastCurrentItemResearchTime;
    public int lastResearchTime;
    private int lastTimer;
    private boolean lastIsResearching;
    public labBlockContainer(InventoryPlayer playerInventory, labBlockTileEntity tileEntity) {
        this.labBlock = tileEntity;
        this.addSlotToContainer(new SlotResearchItem(tileEntity, 0, 38, 46));
        this.addSlotToContainer(new SlotOutput(tileEntity, 1, 118, 46));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slotIndex == 1) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex == 0) {
                if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
                    return null;
                }
            }
            // If we're trying to move from the player's inventory
            else if (labBlockTileEntity.isResearchItem(itemstack1)) {
                // Try to move to input slot
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return null;
                }
            }
            // Moving from player's inventory to player's hotbar or vice versa
            else if (slotIndex >= 2 && slotIndex < 29) {
                if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                    return null;
                }
            } else if (slotIndex >= 29 && slotIndex < 38) {
                if (!this.mergeItemStack(itemstack1, 2, 29, false)) {
                    return null;
                }
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.labBlock.researchTime);
        crafting.sendProgressBarUpdate(this, 0, this.labBlock.currentItemResearchTime);
    }
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting craft = (ICrafting) this.crafters.get(i);

            if (this.lastTimer != this.labBlock.timer) {
                craft.sendProgressBarUpdate(this, 0, this.labBlock.timer);
            }
            if (this.lastIsResearching != this.labBlock.isResearching) {
                craft.sendProgressBarUpdate(this, 1, this.labBlock.isResearching ? 1 : 0);
            }
        }

        this.lastTimer = this.labBlock.timer;
        this.lastIsResearching = this.labBlock.isResearching;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.labBlock.timer = value;
        }
        if (id == 1) {
            this.labBlock.isResearching = value == 1;
        }
    }












    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true; // Allow the player to use the GUI
    }
}
    /*public labBlockContainer(boolean isActive) {
        super(Material.iron);

        this.isActive = isActive;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "labBlockIcon");
    */

