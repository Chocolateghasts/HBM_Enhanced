package com.mewo.hbmenhanced.ResearchBlocks.Tier3;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiT3 extends GuiContainer {
    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/ResearchBlockGuiT3.png");
    public TileEntityT3 tileEntity;
    public GuiT3(InventoryPlayer player, TileEntityT3 te) {
        super(new ContainerT3(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    private void drawEnergyBar() {
        int goalBarX = 178;
        int goalBarY = 38;
        int barX = 5;
        int barY = 29;
        int maxBarHeight = 50;
        float percent = (float) tileEntity.currentEnergy / (float) tileEntity.maxEnergy;
        int barHeight = (int) (percent * maxBarHeight);
        //System.out.println("Percent: " + percent);
        //System.out.println("Height: " + barHeight);
        drawTexturedModalRect(guiLeft + barX, guiTop + barY + (maxBarHeight - barHeight),
                goalBarX, goalBarY + (maxBarHeight - barHeight),
                16, barHeight);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        if (tileEntity.isResearching && tileEntity.maxResearchProgress > 0) {
            int progressWidth = (tileEntity.researchProgress * 25) / tileEntity.maxResearchProgress;
            this.drawTexturedModalRect(
                    guiLeft + 69,
                    guiTop + 28,
                    177,
                    17,
                    progressWidth,
                    17
            );
        }
        drawEnergyBar();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj.drawString("Energy: " + tileEntity.getPower() + "/" + tileEntity.getMaxPower(), 1, 1, 1);
    }
}
