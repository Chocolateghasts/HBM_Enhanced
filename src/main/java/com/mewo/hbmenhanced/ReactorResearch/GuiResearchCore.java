package com.mewo.hbmenhanced.ReactorResearch;

import com.hbm.tileentity.machine.TileEntityReactorResearch;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class GuiResearchCore extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced", "textures/gui/research_core.png");
    private TileEntityResearchCore tileEntity;

    private static final int[] SLOT_X = {12, 12, 134};
    private static final int[] SLOT_Y = {10, 30, 37};
    private static final int SLOT_SIZE = 18;

    public int BUTTON_ID_EXPLODE = 0;

    public GuiResearchCore(InventoryPlayer player, TileEntityResearchCore te) {
        super(new ContainerResearchCore(player, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

//        this.buttonList.clear(); // Clean previous buttons if reopened
//        this.buttonList.add(new GuiButton(BUTTON_ID_EXPLODE, x + 10, y + 130, 80, 20, "Start Research"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = "Research Core";
        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
        TileEntityReactorResearch te = tileEntity.getReactor();
        if (te != null) {
            int infoX = 35;
            int infoY = 25;
            int heat = (int) Math.round((te.heat) * 0.00002 * 980 + 20);
            int maxHeat = (int) Math.round((te.maxHeat) * 0.00002 * 980 + 20);
            fontRendererObj.drawString("Heat: " + heat + " °C" + "/" + maxHeat + "°C", infoX, infoY, 0xFFFFFF);
            fontRendererObj.drawString("Flux: " + te.totalFlux, infoX, infoY + 10, 0xFFFFFF);
            fontRendererObj.drawString("Water: " + te.water, infoX, infoY + 20, 0xFFFFFF);
            fontRendererObj.drawString("Level: " + te.level * 100 + "%", infoX, infoY + 30, 0xFFFFFF);
        }
    }

    private void drawEnergyBar() {
        int currentEnergy = tileEntity.getEnergyStored(ForgeDirection.EAST);
        int maxEnergy = tileEntity.getMaxEnergyStored(ForgeDirection.EAST);

        if (mc.theWorld.isRemote) {
            float progress = (float)currentEnergy / (float)maxEnergy;
            int length = (int)(progress * 52);

            int x = (width - xSize) / 2 + 156;
            int y = (height - ySize) / 2 + 20;

            drawRect(x, y, x + 14, y + 52, 0xFF404040);
            if (length > 0) {
                drawRect(x, y + (52 - length), x + 14, y + 52, 0xFF00FF00);
            }
        }
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
            drawRect(k + SLOT_X[i], l + SLOT_Y[i],
                    k + SLOT_X[i] + SLOT_SIZE + 1, l + SLOT_Y[i] + SLOT_SIZE + 1,
                    0xFF373737);
            // Light inner border
            drawRect(k + SLOT_X[i], l + SLOT_Y[i],
                    k + SLOT_X[i] + SLOT_SIZE, l + SLOT_Y[i] + SLOT_SIZE,
                    0xFF8B8B8B);
            // Slot background
            drawRect(k + SLOT_X[i], l + SLOT_Y[i],
                    k + SLOT_X[i] + SLOT_SIZE - 1, l + SLOT_Y[i] + SLOT_SIZE - 1,
                    0xFF000000);
        }
        drawEnergyBar();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
//        if (button.id == BUTTON_ID_EXPLODE) {
//            tileEntity.getReactor().heat = 999999;
//            tileEntity.getReactor().water = 0;
//            tileEntity.getReactor().level = 999;
//            tileEntity.getReactor().updateEntity();
//            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
//        }
    }

}
