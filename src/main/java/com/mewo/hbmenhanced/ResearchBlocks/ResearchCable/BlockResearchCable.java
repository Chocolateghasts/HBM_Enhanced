package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockResearchCable extends BlockContainer {
    public static int renderId = -1;
    public static IIcon iconCenter;
    public static IIcon iconConnect;
    public static IIcon iconEnd;
    public static IIcon iconConnectVertical;
    public static IIcon iconConnectZ;

    public BlockResearchCable() {
        super(Material.cloth);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(4);
        setResistance(4);
        setBlockName("ResearchBlockT3");
        this.setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconCenter = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "cables/cable_center");
        iconConnect = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "cables/cable_connect_2");
        iconEnd = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "cables/cable_end");
        iconConnectVertical = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "cables/cable_connect_v_2");
        iconConnectZ = iconRegister.registerIcon(hbmenhanced.MODID + ":" + "cables/cable_connect_z");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconCenter;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityResearchCable();
    }

    @Override
    public boolean hasTileEntity(int metaData) {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float[] bounds = calculateBounds(world, x, y, z);
        this.setBlockBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float[] bounds = calculateBounds(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(
                x + bounds[0], y + bounds[1], z + bounds[2],
                x + bounds[3], y + bounds[4], z + bounds[5]);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityResearchCable) {
            ((TileEntityResearchCable) te).updateConnections();
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityResearchCable) {
                TileEntityResearchCable cable = (TileEntityResearchCable) te;
                cable.updateConnections();
            }
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    private float[] calculateBounds(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        float minX = 0.2F, minY = 0.2F, minZ = 0.2F;
        float maxX = 0.8F, maxY = 0.8F, maxZ = 0.8F;

        if (te instanceof TileEntityResearchCable) {
            TileEntityResearchCable cable = (TileEntityResearchCable) te;

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (cable.connections.get(dir)) {
                    switch (dir) {
                        case DOWN:  minY = 0.0F; break;
                        case UP:    maxY = 1.0F; break;
                        case NORTH: minZ = 0.0F; break;
                        case SOUTH: maxZ = 1.0F; break;
                        case WEST:  minX = 0.0F; break;
                        case EAST:  maxX = 1.0F; break;
                    }
                }
            }
        }

        return new float[] {minX, minY, minZ, maxX, maxY, maxZ};
    }
}
