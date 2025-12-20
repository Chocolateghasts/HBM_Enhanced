package com.mewo.hbmenhanced.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class PotionBallsFallenOff extends Potion {

    public PotionBallsFallenOff(int id) {
        super(id, true, 0x3B3B3B);
        this.setPotionName("effect.ballsFallenOff");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity == null) return;

        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect == null) return;

        // Reduced damage: 0.25 every 5 seconds
        if (effect.getDuration() % 100 == 0) {
            entity.attackEntityFrom(DamageSource.generic, 0.25F);
        }

        // Mark finished safely
        if (effect.getDuration() == 1) {
            entity.getEntityData().setBoolean("BallsFallenOffFinished", true);
        }
    }

    public void onPotionAdded(EntityLivingBase entity, PotionEffect effect) {
        entity.getEntityData().setBoolean("HadBallsFallenOff", true);
    }
}
