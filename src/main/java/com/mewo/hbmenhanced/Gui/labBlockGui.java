package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.containers.labBlockContainerBad;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import com.mewo.hbmenhanced.containers.labBlockContainer;


import java.awt.*;

public class labBlockGui {
    /*private static final ResourceLocation LAB_BLOCK_GUI_TEXTURE = new ResourceLocation("hbmenhanced", "textures/gui/LAB_BLOCK_GUI_TEXTURE.png");

    public labBlockTileEntity labBlock;

    public labBlockGui(InventoryPlayer player, labBlockTileEntity entity) {
        super(labBlockContainer.labBlockContainer());
        //super(labBlockContainer(player, entity));

        this.labBlock = entity;
        //super(new labBlockContainerBad(inventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("", 8, 6, 4210752);
        this.fontRendererObj.drawString("", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(LAB_BLOCK_GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }*/
}
