package com.mewo.hbmenhanced.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;

public class EntityStalin extends EntityMob implements IBossDisplayData {

    private boolean angry = false;
    private int beamCooldown = 0;
    private boolean isBeamActive = false;

    public EntityStalin(World world) {
        super(world);
        this.setCustomNameTag("Stalin");
        this.setHealth(this.getMaxHealth());
    }

    public int getTotalArmorValue() {
        return 20;
    }


    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean result = super.attackEntityFrom(source, amount);
        if (!this.worldObj.isRemote && source.getEntity() instanceof EntityPlayer) {
            this.angry = true;
        }
        return result;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (angry && beamCooldown <= 0) {
            EntityPlayer target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
            if (target != null) {
                fireBeam(target);
                beamCooldown = 100; // 5 seconds cooldown
                isBeamActive = true;
            }
        }

        if (isBeamActive) {
            EntityPlayer target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
            if (target != null) {
                // Visual beam effect (similar to beacon)
                for (int i = 0; i < 3; i++) {
                    double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * 0.5D;
                    double d1 = this.posY + this.getEyeHeight();
                    double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * 0.5D;
                    this.worldObj.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }

                // Drain hunger from player
                if (!target.capabilities.isCreativeMode) {
                    target.getFoodStats().addExhaustion(0.5F);
                }
            }
        }

        if (beamCooldown > 0) {
            beamCooldown--;
            if (beamCooldown == 80) { // Stop beam effect after 1 second
                isBeamActive = false;
            }
        }
    }

    private void fireBeam(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            // Visual beam effect at start
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.wither.shoot", 1.0F, 1.0F);
            double dx = player.posX - this.posX;
            double dy = player.posY - this.posY;
            double dz = player.posZ - this.posZ;

            for (int i = 0; i < 10; i++) {
                double progress = i / 10.0D;
                this.worldObj.spawnParticle("reddust",
                        this.posX + dx * progress,
                        this.posY + this.getEyeHeight() + dy * progress,
                        this.posZ + dz * progress,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }
}