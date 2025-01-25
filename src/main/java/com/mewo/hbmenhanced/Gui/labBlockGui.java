package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.containers.labBlockContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class labBlockGui extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("hbmenhanced", "textures/gui/GUI_TEXTURE.png");


    public labBlockGui(InventoryPlayer inventory, World world, int x, int y, int z) {
        super(new labBlockContainer(inventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Lab Block", 8, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
