package com.mewo.hbmenhanced.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
import com.mewo.hbmenhanced.blocks.tileentity.TileEntityTemuSign4;

public class BlockTemuSign4 extends BlockContainer {

    public BlockTemuSign4() {
        super(Material.iron);
        this.setBlockName("temuSign4");
        this.setBlockTextureName("hbmenhanced:temuSign4");
        this.setCreativeTab(com.mewo.hbmenhanced.hbmenhanced.tabhbmenhanced);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null; // Removes hitbox
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTemuSign4();
    }
}