package com.mewo.hbmenhanced.ResearchBlock.Tier2;

import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ResearchBlock extends Block {
    public final int tier;
    public List<ResearchBlock> researchBlocks;

    public ResearchBlock(int tier, String name) {
        super(Material.anvil);
        this.researchBlocks = new ArrayList<>();
        this.tier = tier;

        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(3);
        setResistance(3);
        setBlockName(name);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayer) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityResearchBlock) {
                TileEntityResearchBlock researchBlock = (TileEntityResearchBlock) tile;
                researchBlock.setTeam((EntityPlayer) placer);

                // Check surrounding blocks for existing research blocks
                boolean foundMainBlock = false;
                int[][] offsets = {
                        { 1,  0,  0},
                        {-1,  0,  0},
                        { 0,  0,  1},
                        { 0,  0, -1}
                };

                for (int[] offset : offsets) {
                    int dx = x + offset[0];
                    int dy = y + offset[1];
                    int dz = z + offset[2];

                    TileEntity neighbor = world.getTileEntity(dx, dy, dz);
                    if (neighbor instanceof TileEntityResearchBlock &&
                            ((TileEntityResearchBlock) neighbor).isMainBlock()) {
                        foundMainBlock = true;
                        break;
                    }
                }

                // If no main block found nearby, set this as main block
                if (!foundMainBlock) {
                    researchBlock.setAsMainBlock();
                }
            }
        }
    }

    @Override
    public boolean hasTileEntity(int metaData) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileEntityResearchBlock();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        updateNearbyCores(world, x, y, z);
    }

    private void updateNearbyCores(World world, int x, int y, int z) {
        int[][] offsets = {
                { 1,  0,  0},
                {-1,  0,  0},
                { 0,  0,  1},
                { 0,  0, -1},
                { 0,  1,  0},
                { 0, -1,  0}
        };

        for (int[] offset : offsets) {
            int dx = x + offset[0];
            int dy = y + offset[1];
            int dz = z + offset[2];

            TileEntity te = world.getTileEntity(dx, dy, dz);
            if (te instanceof TileEntityResearchBlock) {
                ((TileEntityResearchBlock) te).updateMultiBlock();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchBlockID, world, x, y, z);
        }
        return true;
    }

}
