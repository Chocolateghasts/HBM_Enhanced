package com.mewo.hbmenhanced.ResearchBlock.Tier2;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiResearchBlock extends GuiContainer {
public TileEntityResearchBlock tileEntity;

    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/ResearchBlockGui.png");

    private static final int[] SLOT_X = {12, 12, 134};
    private static final int[] SLOT_Y = {10, 30, 37};
    private static final int SLOT_SIZE = 18;

    public GuiResearchBlock(InventoryPlayer player, TileEntityResearchBlock te) {
        super(new ContainerResearchBlock(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    private void drawBurnThing(int guiLeft, int guiTop) {
        int burnHeight = tileEntity.getBurnTimeScaled(13);
        int progressWidth = tileEntity.getResearchProgressScaled(24);

        if (burnHeight > 0) {
            this.drawTexturedModalRect(
                    guiLeft + 56,
                    guiTop + 36 + 12 - burnHeight,
                    177,
                    12 - burnHeight,
                    14,
                    burnHeight + 1
            );
        }

        this.drawTexturedModalRect(
                guiLeft + 79,
                guiTop + 34,
                177,
                17,
                progressWidth + 1,
                16
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(texture);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

        int borderColor = 0xFFCCCCCC;
        int fillColor = 0xFF222222;

        for (Object obj : this.inventorySlots.inventorySlots) {
            if (obj instanceof Slot) {
                Slot slot = (Slot) obj;
                int slotX = guiLeft + slot.xDisplayPosition - 1;
                int slotY = guiTop + slot.yDisplayPosition - 1;
                
                drawRect(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, fillColor);

                drawHorizontalLine(slotX, slotX + SLOT_SIZE - 1, slotY, borderColor);
                drawHorizontalLine(slotX, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, borderColor);
                drawVerticalLine(slotX, slotY, slotY + SLOT_SIZE - 1, borderColor);
                drawVerticalLine(slotX + SLOT_SIZE - 1, slotY, slotY + SLOT_SIZE - 1, borderColor);
            }
        }
        if (tileEntity.isResearching) {
            drawBurnThing(guiLeft, guiTop);
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Research Core";
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Tier " + tileEntity.tier, 132, 6, 0x00BFFF);
    }
}
