package com.mewo.hbmenhanced.blocks;

import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockResearchCore extends Block {
    public BlockResearchCore() {
        super(Material.iron);
        setBlockName("Research Core");
        setBlockTextureName(hbmenhanced.MODID + ":research_core");
        setHardness(2.0F);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityResearchCore();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayer) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityResearchCore) {
                ((TileEntityResearchCore) tile).setTeam((EntityPlayer) placer);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityResearchCore) {
                player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchCoreID, world, x, y, z);
            }
        }
        return true;
    }
}