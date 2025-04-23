package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.containers.labBlockContainerBad;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import com.mewo.hbmenhanced.containers.labBlockContainer;

import java.awt.*;

public class labBlockGui extends GuiContainer {
    private static final ResourceLocation LAB_BLOCK_GUI_TEXTURE = new ResourceLocation("hbmenhanced", "textures/gui/LAB_BLOCK_GUI_TEXTURE.png");

    public labBlockTileEntity labBlock;

    public labBlockGui(InventoryPlayer player, labBlockTileEntity entity) {
        super(new labBlockContainer(player, entity));
        this.labBlock = entity;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Lab Block";
        this.fontRendererObj.drawString(name, 8, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Draw the background
        this.mc.getTextureManager().bindTexture(LAB_BLOCK_GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        // Draw progress bar
        if (this.labBlock.isResearching) {
            // Calculate progress (scale to 7 pixels wide)
            int progress = this.labBlock.getResearchTimeRemainingScaled(7);

            // Draw the empty progress bar background
            this.drawTexturedModalRect(
                    this.guiLeft + 79,  // X position on screen
                    this.guiTop + 34,   // Y position on screen
                    40,                 // X position in texture (empty bar at x=40)
                    21,                 // Y position in texture (empty bar at y=21)
                    7,                  // Width of the progress bar (47-40=7 pixels)
                    11                  // Height of the progress bar (32-21=11 pixels)
            );

            // Draw the filled portion using your actual coordinates
            this.drawTexturedModalRect(
                    this.guiLeft + 79,  // Same X position as empty bar
                    this.guiTop + 34,   // Same Y position as empty bar
                    88,                 // X position of filled texture (x=88)
                    72,                 // Y position of filled texture (y=72)
                    progress,           // Width based on research progress
                    11                  // Height (83-72=11 pixels)
            );
        }
    }
}