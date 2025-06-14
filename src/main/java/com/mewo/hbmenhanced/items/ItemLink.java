package com.mewo.hbmenhanced.items;

import com.hbm.blocks.machine.ReactorResearch;
import com.hbm.tileentity.machine.TileEntityReactorResearch;
import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import com.hbm.tileentity.*;

public class ItemLink extends Item {

    public ItemLink() {
        this.setUnlocalizedName("researchReactorLinker");
        this.setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {


        TileEntity te = world.getTileEntity(x, y, z);
        Block blockId = world.getBlock(x, y, z);
        System.out.println(te);
        System.out.println(blockId);
        if (te == null) {
            player.addChatMessage(new ChatComponentText("Could not find Reactor"));
            return false;
        }

        if (te instanceof TileEntityReactorResearch) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }

            nbt.setInteger("hbmenhanced:linkedX", x);
            nbt.setInteger("hbmenhanced:linkedY", y);
            nbt.setInteger("hbmenhanced:linkedZ", z);

            player.addChatMessage(new ChatComponentText("Linked to Research Reactor at " + x + ", " + y + ", " + z));
            return true;
        } else {
            player.addChatMessage(new ChatComponentText("Block is not a Research Reactor"));
            return false;
        }
    }
}
