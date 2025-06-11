package com.mewo.hbmenhanced.ReactorResearch;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiResearchCore extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/research_core.png");
    private TileEntityResearchCore tileEntity;

    private static final int[] SLOT_X = {56, 56, 56};
    private static final int[] SLOT_Y = {17, 35, 53};
    private static final int SLOT_SIZE = 18;

    public GuiResearchCore(InventoryPlayer player, TileEntityResearchCore te) {
        super(new ContainerResearchCore(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Research Core";
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        for (int i = 0; i < 3; i++) {
            // Dark outer border
            drawRect(k + SLOT_X[i] - 1, l + SLOT_Y[i] - 1,
                    k + SLOT_X[i] + SLOT_SIZE + 1, l + SLOT_Y[i] + SLOT_SIZE + 1,
                    0xFF373737);
            // Light inner border
            drawRect(k + SLOT_X[i], l + SLOT_Y[i],
                    k + SLOT_X[i] + SLOT_SIZE, l + SLOT_Y[i] + SLOT_SIZE,
                    0xFF8B8B8B);
            // Slot background
            drawRect(k + SLOT_X[i] + 1, l + SLOT_Y[i] + 1,
                    k + SLOT_X[i] + SLOT_SIZE - 1, l + SLOT_Y[i] + SLOT_SIZE - 1,
                    0xFF000000);
        }

    }

}
