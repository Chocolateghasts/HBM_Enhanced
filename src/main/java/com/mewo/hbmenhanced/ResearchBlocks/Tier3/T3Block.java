package com.mewo.hbmenhanced.ResearchBlocks.Tier3;

import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class T3Block extends BlockContainer {
    private TileEntityT3 te;

    public T3Block() {
        super(Material.anvil);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(4);
        setResistance(4);
        setBlockName("ResearchBlockT3");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityT3) {
                TileEntityT3 te = (TileEntityT3) tileEntity;
                NBTTagCompound nbt = placer.getEntityData();
                te.team = nbt.getString("hbmenhanced:team");
            }
        }
    }

    @Override
    public boolean hasTileEntity(int metaData) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityT3();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchBlockT3ID, world, x, y, z);
        }
        return true;
    }

//    @Override
//    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
//        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
//        TileEntity tileEntity = world.getTileEntity(x, y, z);
//        System.out.println(tileEntity);
//        if (tileEntity instanceof TileEntityT3) {
//            ((TileEntityT3) tileEntity).tryAllSubscriptions();
//        }
//    }
}
