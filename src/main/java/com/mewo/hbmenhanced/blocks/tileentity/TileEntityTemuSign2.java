package com.mewo.hbmenhanced.blocks.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityTemuSign2 extends TileEntity {
    private static final ResourceLocation texture = new ResourceLocation("hbmenhanced:textures/blocks/temunator/sign2.png");

    public ResourceLocation getTexture() {
        return texture;
    }
}