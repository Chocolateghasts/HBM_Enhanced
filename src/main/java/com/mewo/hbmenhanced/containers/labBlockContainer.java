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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.inventory.Container;

public class labBlockContainer extends Container {

    private labBlockTileEntity labBlock;
    public static boolean isActive = false;
    public int lastCurrentItemResearchTime;
    public int lastResearchTime;
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

    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.labBlock.researchTime);
        crafting.sendProgressBarUpdate(this, 0, this.labBlock.currentItemResearchTime);
    }
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);
            if (this.lastResearchTime != this.labBlock.researchTime) {
                crafting.sendProgressBarUpdate(this, 0, this.labBlock.researchTime);
            }
            if (this.lastCurrentItemResearchTime != this.labBlock.currentItemResearchTime) {
                crafting.sendProgressBarUpdate(this, 0, this.labBlock.currentItemResearchTime);
            }
            this.lastResearchTime = this.labBlock.researchTime;
            this.lastCurrentItemResearchTime = this.labBlock.currentItemResearchTime;
        }
    }
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int slot, int newValue) {

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

