package com.mewo.hbmenhanced.blocks;

import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class LabBlock extends Block {

    public LabBlock(Material material) {
        super(material);

        this.setHardness(3.0F);
        this.setResistance(1.0F);
        this.setStepSound(soundTypeAnvil);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ ) {
        if (!world.isRemote) {
            player.addChatMessage(new ChatComponentText("yup works"));

            player.openGui(hbmenhanced.instance, 0, world, x, y, z);
        }
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


    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }

}
