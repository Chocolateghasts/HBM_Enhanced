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
        // Bind background texture
        this.mc.getTextureManager().bindTexture(LAB_BLOCK_GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        // Optional: draw base arrow texture under it
        //this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop + 42, 40, 21, 7, 11);

        if (this.labBlock.isResearching) {
            int arrowX = this.guiLeft + 80;
            int arrowY = this.guiTop + 42;
            int pixelSize = 2;

            // Fill shape: 12 rows of 7 columns
            String[] arrowShape = new String[]{
                    "01100000",
                    "11110000",
                    "11111000",
                    "01111100",
                    "00111110",
                    "00011111",
                    "00011111",
                    "00111110",
                    "01111100",
                    "11111000",
                    "11110000",
                    "01100000"
            };

            // Determine max width (horizontal progress) from 0–7
            int progress = this.labBlock.getResearchTimeRemainingScaled(10);

            for (int row = 0; row < arrowShape.length; row++) {
                String line = arrowShape[row];
                for (int col = 0; col < progress; col++) {
                    if (col < line.length() && line.charAt(col) == '1') {
                        int x = arrowX + col * pixelSize;
                        int y = arrowY + row * pixelSize;
                        drawRect(x, y, x + pixelSize, y + pixelSize, 0xFF00FF00); // Green
                    }
                }
            }
        }
    }
}