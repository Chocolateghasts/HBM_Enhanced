package com.mewo.hbmenhanced;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class OreBlock extends Block {

    public extends Block {
        super(Material);

        this.setHardness(3.0F);
        this.setResistance(1.0F);
        this.setStepSound(soundTypeWood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    protected OreBlock(Material p_i45394_1_) {
        super(p_i45394_1_);
    }
}
