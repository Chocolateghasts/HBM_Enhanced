package com.mewo.hbmenhanced.potion;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionNoBalls extends Potion {

    private static final Random rand = new Random();
    private static final String TAG_NEXT = "NoBallsNext";

    public PotionNoBalls(int id) {
        super(id, true, 0x1A1A1A);
        this.setPotionName("effect.noBalls");
    }

    public void onPotionAdded(EntityLivingBase entity, PotionEffect effect) {
        if (entity == null) return;
        entity.getEntityData().setBoolean("HadNoBalls", true);
        scheduleNext(entity);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) return;

        long now = entity.worldObj.getTotalWorldTime();
        long next = entity.getEntityData().getLong(TAG_NEXT);

        if (now >= next) {
            entity.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.confusion.id, 600, 0));
            scheduleNext(entity);
        }
    }

    private void scheduleNext(EntityLivingBase entity) {
        int delay = 6000 + rand.nextInt(6000); // 5–10 minutes
        entity.getEntityData().setLong(TAG_NEXT, entity.worldObj.getTotalWorldTime() + delay);
    }
}
