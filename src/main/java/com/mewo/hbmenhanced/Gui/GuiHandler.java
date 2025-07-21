package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.ReactorResearch.ContainerResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.GuiResearchCore;
import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.ContainerResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.GuiResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.TileEntityResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.ContainerT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.GuiT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.ContainerT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.GuiT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.ContainerT3;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.GuiT3;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
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
            case 3:
                if (tileEntity instanceof TileEntityT2) {
                    return new ContainerT2(player.inventory, (TileEntityT2) tileEntity);
                }
                break;
            case 4:
                if (tileEntity instanceof TileEntityT3) {
                    return new ContainerT3(player.inventory, (TileEntityT3) tileEntity);
                }
                break;
            case 5:
                if (tileEntity instanceof TileEntityResearchController) {
                    return new ContainerResearchController(player.inventory, (TileEntityResearchController) tileEntity);
                }
                break;
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
            case 3:
                if (tileEntity instanceof TileEntityT2) {
                    return new GuiT2(player.inventory, (TileEntityT2) tileEntity);
                }
                break;
            case 4:
                if (tileEntity instanceof TileEntityT3) {
                    return new GuiT3(player.inventory, (TileEntityT3) tileEntity);
                }
                break;
            case 5:
                if (tileEntity instanceof TileEntityResearchController) {
                    return new GuiResearchController(player.inventory, (TileEntityResearchController) tileEntity);
                }
                break;
        }
        return null;
    }
}