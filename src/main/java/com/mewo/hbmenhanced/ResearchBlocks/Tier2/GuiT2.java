package com.mewo.hbmenhanced.ResearchBlocks.Tier2;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTank;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiT2 extends GuiContainer {
    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/ResearchBlockGuiT2.png");


    public TileEntityT2 tileEntity;

    public GuiT2(InventoryPlayer player, TileEntityT2 te) {
        super(new ContainerT2(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    public void drawFluidBar() {
        FluidTank tank = tileEntity.tank;
        int fill = tank.getFill();
        int maxFill = tank.getMaxFill();
        if (maxFill == 0 || fill == 0) return;

        FluidType fluid = tank.getTankType();
        if (fluid == null) return;

        int barHeight = 50;
        int filledHeight = (int) ((fill / (float) maxFill) * barHeight);

        int barX = guiLeft + 5;
        int barY = guiTop + 29 + (barHeight - filledHeight);

        // Bind texture
        mc.getTextureManager().bindTexture(fluid.getTexture());
        // Set tint color
        int color = fluid.getTint();
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GL11.glColor3f(r, g, b);

        int texSize = 16; // or 32 if your texture is 32x32
        func_146110_a(
                barX, barY,       // draw position on screen
                0, 0,             // start at top-left of texture
                16, filledHeight, // draw 16px wide, variable height
                texSize, texSize  // texture resolution
        );

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        drawFluidBar();
        this.mc.getTextureManager().bindTexture(texture);
        if (tileEntity.isBurning) {
            drawTexturedModalRect(guiLeft + 53, guiTop + 47, 177, 0, 11, 13);
        }
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
    }
}