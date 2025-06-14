package com.mewo.hbmenhanced.ResearchBlock;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiResearchBlock extends GuiContainer {
public TileEntityResearchBlock tileEntity;

    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/research_block.png");

    private static final int[] SLOT_X = {12, 12, 134};
    private static final int[] SLOT_Y = {10, 30, 37};
    private static final int SLOT_SIZE = 18;

    public GuiResearchBlock(InventoryPlayer player, TileEntityResearchBlock te) {
        super(new ContainerResearchBlock(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

        for (Object obj : this.inventorySlots.inventorySlots) {
            if (obj instanceof Slot) {
                Slot slot = (Slot) obj;
                int slotX = guiLeft + slot.xDisplayPosition;
                int slotY = guiTop + slot.yDisplayPosition;

                drawRect(slotX, slotY, slotX + SLOT_SIZE + 1, slotY + SLOT_SIZE + 1, 0xFF373737);
                drawRect(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF8B8B8B);
                drawRect(slotX, slotY, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, 0xFF000000);
            }
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Research Core";
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
    }
}
