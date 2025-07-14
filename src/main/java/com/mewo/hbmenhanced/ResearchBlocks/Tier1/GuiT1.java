package com.mewo.hbmenhanced.ResearchBlocks.Tier1;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiT1 extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/ResearchBlockGui.png");

    private static final int SLOT_SIZE = 18;
    private TileEntityT1 tileEntity;


    public GuiT1(InventoryPlayer player, TileEntityT1 te) {
        super(new ContainerT1(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

        if (tileEntity.isBurning && tileEntity.totalBurnTime > 0) {
            int flameHeight = 14;  // full flame height in texture & on screen
            int burnHeight = (tileEntity.currentBurnTime * flameHeight) / tileEntity.totalBurnTime;
            int yPos = guiTop + 61 - burnHeight;  // 61 should be guiTop + 47 + flameHeight (47 + 14)
            this.drawTexturedModalRect(guiLeft + 12, yPos, 177, flameHeight - burnHeight, 11, burnHeight);

        }

        if (tileEntity.isResearching && tileEntity.maxResearchProgress > 0) {
            int progressWidth = (tileEntity.researchProgress * 25) / tileEntity.maxResearchProgress;
            this.drawTexturedModalRect(
                    guiLeft + 28,
                    guiTop + 28,
                    177,
                    17,
                    progressWidth,
                    17
            );
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Research Core";
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Tier 1", 132, 6, 0x00BFFF);
    }
}
