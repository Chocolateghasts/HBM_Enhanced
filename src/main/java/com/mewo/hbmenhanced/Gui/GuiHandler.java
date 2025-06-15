package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.ReactorResearch.ContainerResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.GuiResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.ResearchBlock.ContainerResearchBlock;
import com.mewo.hbmenhanced.ResearchBlock.GuiResearchBlock;
import com.mewo.hbmenhanced.ResearchBlock.TileEntityResearchBlock;
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
                if (tileEntity instanceof TileEntityResearchBlock) {
                    return new ContainerResearchBlock(player.inventory, (TileEntityResearchBlock) tileEntity);
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
                if (tileEntity instanceof TileEntityResearchBlock) {
                    return new GuiResearchBlock(player.inventory, (TileEntityResearchBlock) tileEntity);
                }
                break;
        }
        return null;
    }
}