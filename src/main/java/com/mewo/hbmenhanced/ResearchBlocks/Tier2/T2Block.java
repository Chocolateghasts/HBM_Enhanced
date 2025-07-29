package com.mewo.hbmenhanced.ResearchBlocks.Tier2;

import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class T2Block extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon sides;
    @SideOnly(Side.CLIENT)
    private IIcon top;

    public T2Block() {
        super(Material.anvil);
        setCreativeTab(hbmenhanced.tabhbmenhanced);
        setHardness(4);
        setResistance(4);
        setBlockName("ResearchBlockT2");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote && placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityT2) {
                TileEntityT2 te = (TileEntityT2) tileEntity;
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
        return new TileEntityT2();
    }

//    @Override
//    public TileEntity createNewTileEntity(World world, int meta) {
//        return new TileEntityT2();
//    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            System.out.println("Opening T2 GUI...");
            player.openGui(hbmenhanced.instance, hbmenhanced.guiResearchBlockT2ID, world, x, y, z);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        this.blockIcon = register.registerIcon(hbmenhanced.MODID + ":" + "research_controller");
        this.top = register.registerIcon(hbmenhanced.MODID + ":" + "research_top");
        this.sides = register.registerIcon(hbmenhanced.MODID + ":" + "t2");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int p_149691_2_) {
        switch (side) {
            case 0: // Bottom
                return this.top;
            case 1: // Top
                return this.top;
            case 2: // North
                return this.sides;
            case 3: // South
                return this.sides;
            case 4: // West
                return this.sides;
            case 5: // East
                return this.sides;
            default:
                return this.blockIcon;
        }
    }
}