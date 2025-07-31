package com.mewo.hbmenhanced.ResearchBlocks.ResearchSource;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockResearchSource extends BlockContainer {

    public BlockResearchSource() {
        super(Material.anvil);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityResearchSource();
    }
}
