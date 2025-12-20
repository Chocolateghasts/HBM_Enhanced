package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.blocks.tileentity.TileEntityWashingMachine;
import com.mewo.hbmenhanced.containers.ContainerWashingMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWashingMachine extends GuiContainer {

    private static final ResourceLocation TEX =
            new ResourceLocation("hbmenhanced", "textures/gui/gui_washingmachine.png");

    private TileEntityWashingMachine tile;

    public GuiWashingMachine(InventoryPlayer player, TileEntityWashingMachine tile) {
        super(new ContainerWashingMachine(player, tile));
        this.tile = tile;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
        mc.getTextureManager().bindTexture(TEX);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Progress bar
        int progress = tile.getWashTimeScaled(24);
        drawTexturedModalRect(
                guiLeft + 79,
                guiTop + 34,
                176,
                0,
                progress + 1,
                16
        );
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        fontRendererObj.drawString("Washing Machine", 8, 6, 4210752);
    }
}
