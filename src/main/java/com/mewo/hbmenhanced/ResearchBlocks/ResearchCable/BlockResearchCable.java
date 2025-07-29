package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockResearchCable extends BlockContainer {
    protected BlockResearchCable() {
        super(Material.cloth);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityResearchCable();
    }
}
