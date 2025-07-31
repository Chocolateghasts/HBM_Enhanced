package com.mewo.hbmenhanced.ResearchBlocks.ResearchCable;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class CableRenderer implements ISimpleBlockRenderingHandler {
    public static int renderId;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        block.setBlockBounds(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
        renderer.setRenderBoundsFromBlock(block);

        Tessellator tess = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tess.startDrawingQuads();
        tess.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
        tess.draw();

        GL11.glPopMatrix();
        block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
    }



    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
                                    Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockResearchCable))
            return false;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityResearchCable))
            return false;

        TileEntityResearchCable cable = (TileEntityResearchCable) te;

        // Render the center cube with center texture
        block.setBlockBounds(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
        renderer.setRenderBoundsFromBlock(block);
        renderer.setOverrideBlockTexture(BlockResearchCable.iconCenter);
        renderer.renderStandardBlock(block, x, y, z);

        // Render connection arms
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (cable.connections.get(dir)) {
                float minX = 0.375F, minY = 0.375F, minZ = 0.375F;
                float maxX = 0.625F, maxY = 0.625F, maxZ = 0.625F;

                switch (dir) {
                    case DOWN:
                        minY = 0.0F;
                        maxY = 0.375F;
                        break;
                    case UP:
                        minY = 0.625F;
                        maxY = 1.0F;
                        break;
                    case NORTH:
                        minZ = 0.0F;
                        maxZ = 0.375F;
                        break;
                    case SOUTH:
                        minZ = 0.625F;
                        maxZ = 1.0F;
                        break;
                    case WEST:
                        minX = 0.0F;
                        maxX = 0.375F;
                        break;
                    case EAST:
                        minX = 0.625F;
                        maxX = 1.0F;
                        break;
                }

                block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                renderer.setRenderBoundsFromBlock(block);

                if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
                    renderer.setOverrideBlockTexture(BlockResearchCable.iconConnectVertical);
                } else if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
                    renderer.setOverrideBlockTexture(BlockResearchCable.iconConnectZ);
                } else {
                    renderer.setOverrideBlockTexture(BlockResearchCable.iconConnect);
                }
                renderer.renderStandardBlock(block, x, y, z);
            }
        }

        // Reset to defaults
        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        return true;
    }



    @Override public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override public int getRenderId() {
        return BlockResearchCable.renderId;
    }
}
