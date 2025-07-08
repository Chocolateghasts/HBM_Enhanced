package com.mewo.hbmenhanced.ResearchBlocks.Tier1;

import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class T1Block extends Block {
    public T1Block() {
        super(Material.anvil);

        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(3);
        setResistance(3);
        setBlockName("ResearchBlockT1");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityT1) {
                TileEntityT1 te = (TileEntityT1) tileEntity;
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
    public TileEntity createTileEntity(World world, int meta) {
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            return true;
        }
        return true;
    }

}
