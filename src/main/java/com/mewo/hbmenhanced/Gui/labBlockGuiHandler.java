package com.mewo.hbmenhanced.Gui;

import com.hbm.handler.GUIHandler;
import com.mewo.hbmenhanced.containers.labBlockContainer;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import static com.mewo.hbmenhanced.hbmenhanced.labBlock;

public class labBlockGuiHandler extends GUIHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            switch (ID) {
                case hbmenhanced.guiLabBlockID:
                    if (tileEntity instanceof labBlockTileEntity) {
                    return new labBlockContainer(player.inventory, (labBlockTileEntity) tileEntity);
                }
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                switch (ID) {
                    case hbmenhanced.guiLabBlockID:
                        if (tileEntity instanceof labBlockTileEntity) {
                            return new labBlockGui(player.inventory, (labBlockTileEntity) tileEntity);
                        }
                }
            }
            return null;
    }
}
