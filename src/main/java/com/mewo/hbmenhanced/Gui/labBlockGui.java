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

    private void drawGame() {
        int paddleX = this.guiLeft + 120;
        int paddleY = this.guiTop + 140;

        int ballX = this.guiLeft + 90;
        int ballY = this.guiTop + 100;

        drawRect(paddleX, paddleY, paddleX + 5, paddleY + 1, 0xFF0000FF);
        drawRect(ballX, ballY, 2, 2, 0xFFFFFF00);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        boolean shouldPlayGame = false;
        this.mc.getTextureManager().bindTexture(LAB_BLOCK_GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

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

//        if (shouldPlayGame) {
//            shouldPlayGame = false;
//            drawGame();
//        }
    }
}