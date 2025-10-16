package com.mewo.hbmenhanced.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityStalin extends EntityLiving {

    public EntityStalin(World world) {
        super(world);
        this.setCustomNameTag("Stalin");   // Name displayed above entity
        this.setAlwaysRenderNameTag(true);
        this.setSize(0.6F, 1.8F);          // Width & height
        this.experienceValue = 5;          // XP dropped
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        // Always set attributes here, NOT in the constructor
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);

        // Set current health after max health is set
        this.setHealth(20.0F);
    }
}
