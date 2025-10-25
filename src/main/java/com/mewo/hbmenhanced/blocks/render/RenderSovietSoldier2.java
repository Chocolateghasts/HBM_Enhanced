package com.mewo.hbmenhanced.blocks.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class RenderSovietSoldier2 extends RenderBiped {

    private static final ResourceLocation SOLDIER_TEXTURE = new ResourceLocation("hbmenhanced:textures/entity/soviet_soldier.png");

    public RenderSovietSoldier2() {
        super(new ModelBiped(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return SOLDIER_TEXTURE;
    }
}
