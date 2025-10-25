package com.mewo.hbmenhanced.blocks.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class RenderSovietOfficer extends RenderBiped {

    private static final ResourceLocation OFFICER_TEXTURE = new ResourceLocation("hbmenhanced:textures/entity/soviet_officer.png");

    public RenderSovietOfficer() {
        super(new ModelBiped(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return OFFICER_TEXTURE;
    }
}
