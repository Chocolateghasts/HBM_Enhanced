package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.containers.labBlockContainer;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class labBlockGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof labBlockTileEntity) {
                return new labBlockContainer(player.inventory, world, x, y, z);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof labBlockTileEntity) {
                return new labBlockGui(player.inventory, world, x, y, z);
            }
        }
        return null;
    }
}
