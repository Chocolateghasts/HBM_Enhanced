package com.mewo.hbmenhanced.blocks;

import com.mewo.hbmenhanced.hbmenhanced;
import com.mewo.hbmenhanced.blocks.tileentity.TileEntityWashingMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class WashingMachine extends BlockContainer {

    private IIcon side1, side2, top, bottom;

    public WashingMachine() {
        super(Material.iron);
        setBlockName("washingMachine");
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWashingMachine();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                                    EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

        if (!world.isRemote) {
            player.openGui(
                    hbmenhanced.instance, // your main mod instance
                    7,                    // GUI ID
                    world,
                    x, y, z
            );
        }
        return true;
    }


    @Override
    public void registerBlockIcons(IIconRegister reg) {
        side1 = reg.registerIcon("hbmenhanced:washingmachine_side1");
        side2 = reg.registerIcon("hbmenhanced:washingmachine_side2");
        top = reg.registerIcon("hbmenhanced:washingmachine_top");
        bottom = reg.registerIcon("hbmenhanced:washingmachine_bottom");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) return bottom;
        if (side == 1) return top;
        return side == 3 ? side1 : side2;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
                                EntityLivingBase entity, ItemStack stack) {

        int dir = Math.round(entity.rotationYaw / 90.0F) & 3;
        world.setBlockMetadataWithNotify(x, y, z, dir, 2);
    }
}
