package com.mewo.hbmenhanced.ResearchBlocks.ResearchController;

import com.mewo.hbmenhanced.ResearchBlocks.Tier3.ContainerT3;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiResearchController extends GuiContainer {
    private TileEntityResearchController tileEntity;
    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/controller/ResearchController.png");
    public GuiResearchController(InventoryPlayer player, TileEntityResearchController te) {
        super(new ContainerResearchController(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }
}
