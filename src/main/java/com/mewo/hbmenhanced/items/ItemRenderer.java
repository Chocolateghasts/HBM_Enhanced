package com.mewo.hbmenhanced.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {

    private static final RenderItem renderItem = new RenderItem();

    public void renderItemInGUI(ItemStack itemStack, int x, int y) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        renderItem.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                Minecraft.getMinecraft().getTextureManager(),
                itemStack, 0, 0);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
