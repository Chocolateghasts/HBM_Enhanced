package com.mewo.hbmenhanced.blocks.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import com.mewo.hbmenhanced.blocks.tileentity.TileEntityTemuSign6;

public class RenderTemuSign6 extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileEntityTemuSign6))
            return;

        TileEntityTemuSign6 sign = (TileEntityTemuSign6) te;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.95);
        GL11.glRotatef(180, 0, 1, 0);

        GL11.glDisable(GL11.GL_LIGHTING);
        this.bindTexture(sign.getTexture());

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-0.5F, -0.5F, 0);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(0.5F, -0.5F, 0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(0.5F, 0.5F, 0);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-0.5F, 0.5F, 0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}