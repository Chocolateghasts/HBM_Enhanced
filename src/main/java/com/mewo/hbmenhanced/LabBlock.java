package com.mewo.hbmenhanced;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;

public class LabBlock extends Block {

    protected LabBlock(Material material) {
        super(material);

        this.setHardness(3.0F);
        this.setResistance(1.0F);
        this.setStepSound(soundTypeWood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }

}
