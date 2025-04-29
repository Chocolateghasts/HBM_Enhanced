package com.mewo.hbmenhanced.blocks;

import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import net.minecraft.block.BlockFire;
import com.mewo.hbmenhanced.containers.labBlockContainer;
import scala.reflect.internal.Trees;


import java.util.Timer;
import java.util.TimerTask;

import static com.mewo.hbmenhanced.containers.labBlockContainer.isActive;


public class LabBlock extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBack;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconWest;
    @SideOnly(Side.CLIENT)
    private IIcon icontop2;
//    @SideOnly(Side.CLIENT)
//    @SideOnly(Side.CLIENT)
//    @SideOnly(Side.CLIENT)

    private static int frame;
    private Timer timer;
    private World world;
    private int x, y, z;





    public LabBlock(Material material) {
        super(Material.anvil);
        this.setHardness(3.0F);
        this.setResistance(1.0F);
        this.setStepSound(soundTypeAnvil);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        player.openGui(hbmenhanced.instance, hbmenhanced.guiLabBlockID, world, x, y, z);
        return true;
    }


    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new labBlockTileEntity();


    }

public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack item) {
        int l = MathHelper.floor_double((double)(entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }
        if (l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }
        if (l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }
        if (l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }
}

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        super.onBlockPreDestroy(world, x, y, z, meta);
        if (world.getTileEntity(x, y, z) instanceof labBlockTileEntity) {
            ((labBlockTileEntity)world.getTileEntity(x, y, z)).isResearching = false;
        }
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(hbmenhanced.MODID + ":" + "sideb");
        this.iconFront = register.registerIcon(hbmenhanced.MODID + ":" + "sideb");
        this.iconBack = register.registerIcon(hbmenhanced.MODID + ":" + "back");
        this.iconTop = register.registerIcon(hbmenhanced.MODID + ":" + "top");
        this.iconBottom = register.registerIcon(hbmenhanced.MODID + ":" + "bottom");
        this.iconWest = register.registerIcon(hbmenhanced.MODID + ":" + "sidea");
    }
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        TileEntity te = world != null ? world.getTileEntity(x, y, z) : null;
        boolean isResearching = false;

        if (te instanceof labBlockTileEntity) {
            isResearching = ((labBlockTileEntity) te).isResearching;
        }

        switch (side) {
            case 0: // Bottom
                return this.iconBottom;
            case 1: // Top
                return this.iconTop;
            case 2: // North
                return this.iconFront;
            case 3: // South
                return this.iconBack;
            case 4: // West
                return this.iconWest;
            case 5: // East
                return this.iconWest;
            default:
                return this.blockIcon;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
