package com.mewo.hbmenhanced.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityStalinBeam extends Entity {

    private EntityLivingBase shooter;
    private int beamLifetime = 20; // 1 second beam duration

    public EntityStalinBeam(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityStalinBeam(World world, EntityLivingBase shooter) {
        this(world);
        this.shooter = shooter;
        this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);

        // Calculate beam direction
        double distance = 16.0D; // Maximum beam reach
        this.motionX = -Math.sin(Math.toRadians(shooter.rotationYaw)) * Math.cos(Math.toRadians(shooter.rotationPitch)) * distance;
        this.motionY = -Math.sin(Math.toRadians(shooter.rotationPitch)) * distance;
        this.motionZ = Math.cos(Math.toRadians(shooter.rotationYaw)) * Math.cos(Math.toRadians(shooter.rotationPitch)) * distance;
    }

    @Override
    public void onUpdate() {
        if (this.ticksExisted > beamLifetime) {
            this.setDead();
            return;
        }

        // Create beacon-like beam effect
        if (shooter != null) {
            Vec3 start = Vec3.createVectorHelper(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
            Vec3 end = Vec3.createVectorHelper(
                    shooter.posX + this.motionX,
                    shooter.posY + shooter.getEyeHeight() + this.motionY,
                    shooter.posZ + this.motionZ
            );

            // Spawn particles along the beam
            for (int i = 0; i < 20; i++) {
                double progress = i / 20.0D;
                double px = start.xCoord + (end.xCoord - start.xCoord) * progress;
                double py = start.yCoord + (end.yCoord - start.yCoord) * progress;
                double pz = start.zCoord + (end.zCoord - start.zCoord) * progress;

                this.worldObj.spawnParticle("reddust", px, py, pz, 0.0D, 0.0D, 0.0D);
            }

            // Check for players in beam path
            MovingObjectPosition hit = this.worldObj.rayTraceBlocks(start, end);
            if (hit != null) {
                this.checkBeamCollision(start, Vec3.createVectorHelper(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord));
            } else {
                this.checkBeamCollision(start, end);
            }
        }

        // Play beam sound
        if (this.ticksExisted == 1) {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.wither.shoot", 1.0F, 1.0F);
        }
    }

    private void checkBeamCollision(Vec3 start, Vec3 end) {
        double range = 1.0D; // Beam thickness
        for (Object obj : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(range, range, range))) {
            EntityPlayer player = (EntityPlayer) obj;
            if (!player.capabilities.isCreativeMode) {
                player.getFoodStats().addExhaustion(1.0F);
            }
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}
}