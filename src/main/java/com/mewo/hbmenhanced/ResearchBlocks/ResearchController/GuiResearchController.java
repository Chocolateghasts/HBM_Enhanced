package com.mewo.hbmenhanced.ResearchBlocks.ResearchController;

import com.mewo.hbmenhanced.ResearchBlocks.Tier3.ContainerT3;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class GuiResearchController extends GuiContainer {
    private TileEntityResearchController tileEntity;
    public GuiResearchController(InventoryPlayer player, TileEntityResearchController te) {
        super(new ContainerResearchController(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

    }
}
