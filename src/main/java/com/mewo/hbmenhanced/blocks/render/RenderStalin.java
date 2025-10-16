package com.mewo.hbmenhanced.blocks.render;

import com.mewo.hbmenhanced.entity.EntityStalin;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderStalin extends RenderBiped {

    private static final ResourceLocation STALIN_TEXTURE = new ResourceLocation("hbmenhanced:textures/entity/stalin.png");

    public RenderStalin() {
        super(new ModelBiped(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return STALIN_TEXTURE;
    }
}