package com.mewo.hbmenhanced.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityStalin extends EntityMob implements IBossDisplayData {

    private boolean angry = false;
    private int beamCooldown = 0;
    private boolean isBeamActive = false;
    private int summonCooldown = 0; // 25 seconds = 500 ticks

    public EntityStalin(World world) {
        super(world);
        this.setCustomNameTag("Stalin");
        this.setHealth(this.getMaxHealth());
    }

    @Override
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
        if (!this.worldObj.isRemote && source.getEntity() instanceof EntityPlayer && !this.angry) {
            this.angry = true;
            // Spawn 2 groups immediately when Stalin gets angry
            summonGroup();
            summonGroup();
        }
        return result;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (angry) {
            // Handle beam attack
            if (beamCooldown <= 0) {
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
                    for (int i = 0; i < 3; i++) {
                        double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * 0.5D;
                        double d1 = this.posY + this.getEyeHeight();
                        double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * 0.5D;
                        this.worldObj.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }

                    if (!target.capabilities.isCreativeMode) {
                        target.getFoodStats().addExhaustion(0.5F);
                    }
                }
            }

            if (beamCooldown > 0) {
                beamCooldown--;
                if (beamCooldown == 80) {
                    isBeamActive = false;
                }
            }

            // Handle soldier summoning every 25 seconds
            if (summonCooldown > 0) {
                summonCooldown--;
            } else {
                summonGroup();
                summonCooldown = 500; // reset 25s cooldown
            }
        }
    }

    private void fireBeam(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
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

    /** Randomly choose and spawn one of three soldier groups */
    private void summonGroup() {
        if (this.worldObj.isRemote) return;

        int choice = this.rand.nextInt(3) + 1;

        switch (choice) {
            case 1:
                // Option 1: 4 Solder, 2 Soldier1, 1 Soldier2, 1 Officer
                spawnSoldiers(EntitySovietSoldier.class, 4);
                spawnSoldiers(EntitySovietSoldier1.class, 2);
                spawnSoldiers(EntitySovietSoldier2.class, 1);
                spawnSoldiers(EntitySovietOfficer.class, 1);
                break;
            case 2:
                // Option 2: 8 Soldier, 6 Soldier1, 1 Officer
                spawnSoldiers(EntitySovietSoldier.class, 8);
                spawnSoldiers(EntitySovietSoldier1.class, 6);
                spawnSoldiers(EntitySovietOfficer.class, 1);
                break;
            case 3:
                // Option 3: 5 Soldier, 3 Soldier1, 3 Soldier2, 1 Officer
                spawnSoldiers(EntitySovietSoldier.class, 5);
                spawnSoldiers(EntitySovietSoldier1.class, 3);
                spawnSoldiers(EntitySovietSoldier2.class, 3);
                spawnSoldiers(EntitySovietOfficer.class, 1);
                break;
        }
    }

    /** Helper to spawn N soldiers of a given type */
    private void spawnSoldiers(Class<? extends EntityMob> clazz, int count) {
        try {
            for (int i = 0; i < count; i++) {
                EntityMob soldier = clazz.getConstructor(World.class).newInstance(this.worldObj);
                double offsetX = (this.rand.nextDouble() - 0.5D) * 6.0D;
                double offsetZ = (this.rand.nextDouble() - 0.5D) * 6.0D;
                soldier.setLocationAndAngles(this.posX + offsetX, this.posY, this.posZ + offsetZ, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(soldier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
