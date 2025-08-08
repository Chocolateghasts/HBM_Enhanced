package com.mewo.hbmenhanced.ResearchBlocks.Util.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiResearchCategoryButton extends GuiButton {
    public String categoryId;

    public GuiResearchCategoryButton(int id, int x, int y, String upperCaseName, FontRenderer fontRendererObj) {
        super(id, x, y, calculateWidth(fontRendererObj, upperCaseName), 12, upperCaseName);
        this.categoryId = upperCaseName.toLowerCase();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
    }

    private static int calculateWidth(FontRenderer fontRenderer, String text) {
        return fontRenderer.getStringWidth(text);
    }
}
