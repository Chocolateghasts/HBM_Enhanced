package com.mewo.hbmenhanced.containers;

import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.inventory.Container;

public class labBlockContainer extends Container {

    private labBlockTileEntity labBlock;
    public static boolean isActive = false;
    public labBlockContainer(InventoryPlayer inventory, labBlockTileEntity tileEntity) {
        this.labBlock = tileEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return false;
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

