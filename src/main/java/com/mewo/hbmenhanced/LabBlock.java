package com.mewo.hbmenhanced;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class LabBlock extends Block {

    protected LabBlock(Material material) {
        super(material);

        this.setHardness(3.0F);
        this.setResistance(1.0F);
        this.setStepSound(soundTypeWood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

}
