package com.mewo.hbmenhanced.potion;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PotionProtectionHandler {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) return;

        // Handle BallCancer → Balls Fallen Off safely
        if (entity.getEntityData().getBoolean("BallCancerFinished")) {
            entity.getEntityData().removeTag("BallCancerFinished");

            if (ModPotions.ballsFallenOff != null &&
                    !entity.isPotionActive(ModPotions.ballsFallenOff)) {

                entity.addPotionEffect(new PotionEffect(ModPotions.ballsFallenOff.id, 3600, 0));
            }
        }

        // Handle Balls Fallen Off → No Balls safely
        if (entity.getEntityData().getBoolean("BallsFallenOffFinished")) {
            entity.getEntityData().removeTag("BallsFallenOffFinished");

            if (ModPotions.noBalls != null &&
                    !entity.isPotionActive(ModPotions.noBalls)) {

                entity.addPotionEffect(new PotionEffect(ModPotions.noBalls.id, 72000, 0));
            }
        }

        // Reapply potions if missing but NBT says Had
        if (ModPotions.ballCancer != null &&
                !entity.isPotionActive(ModPotions.ballCancer) &&
                entity.getEntityData().getBoolean("HadBallCancer")) {
            entity.addPotionEffect(new PotionEffect(ModPotions.ballCancer.id, 200, 0));
        }

        if (ModPotions.ballsFallenOff != null &&
                !entity.isPotionActive(ModPotions.ballsFallenOff) &&
                entity.getEntityData().getBoolean("HadBallsFallenOff")) {
            entity.addPotionEffect(new PotionEffect(ModPotions.ballsFallenOff.id, 200, 0));
        }

        if (ModPotions.noBalls != null &&
                !entity.isPotionActive(ModPotions.noBalls) &&
                entity.getEntityData().getBoolean("HadNoBalls")) {
            entity.addPotionEffect(new PotionEffect(ModPotions.noBalls.id, 72000, 0));
        }
    }
}
