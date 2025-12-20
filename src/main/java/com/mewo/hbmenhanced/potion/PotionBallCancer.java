package com.mewo.hbmenhanced.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class PotionBallCancer extends Potion {

    public PotionBallCancer(int id) {
        super(id, true, 0x7A1C1C);
        this.setPotionName("effect.ballCancer");
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

        // Reduced damage: 0.5 per tick every 2 seconds
        if (effect.getDuration() % 40 == 0) {
            entity.attackEntityFrom(DamageSource.magic, 0.5F);

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).getFoodStats().addExhaustion(0.5F);
            }
        }

        // Mark finished safely
        if (effect.getDuration() == 1) {
            entity.getEntityData().setBoolean("BallCancerFinished", true);
        }
    }

    public void onPotionAdded(EntityLivingBase entity, PotionEffect effect) {
        entity.getEntityData().setBoolean("HadBallCancer", true);
    }
}
