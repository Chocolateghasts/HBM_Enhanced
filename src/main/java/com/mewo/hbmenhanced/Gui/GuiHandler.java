package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.ReactorResearch.ContainerResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.GuiResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.ContainerT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.GuiT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.containers.labBlockContainer;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch(ID) {
            case 0: // Lab Block GUI
                if (tileEntity instanceof labBlockTileEntity) {
                    return new labBlockContainer(player.inventory, (labBlockTileEntity) tileEntity);
                }
                break;
            case 1: // Research Core GUI
                if (tileEntity instanceof TileEntityResearchCore) {
                    return new ContainerResearchCore(player.inventory, (TileEntityResearchCore) tileEntity);
                }
                break;
            case 2:
                if (tileEntity instanceof TileEntityT1) {
                    return new ContainerT1(player.inventory, (TileEntityT1) tileEntity);
                }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch(ID) {
            case 0: // Lab Block GUI
                if (tileEntity instanceof labBlockTileEntity) {
                    return new labBlockGui(player.inventory, (labBlockTileEntity) tileEntity);
                }
                break;
            case 1: // Research Core GUI
                if (tileEntity instanceof TileEntityResearchCore) {
                    return new GuiResearchCore(player.inventory, (TileEntityResearchCore) tileEntity);
                }
                break;
            case 2:
                if (tileEntity instanceof TileEntityT1) {
                    return new GuiT1(player.inventory, (TileEntityT1) tileEntity);
                }
                break;
        }
        return null;
    }
}