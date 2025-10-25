package com.mewo.hbmenhanced.entity;

import com.hbm.entity.projectile.EntityBullet;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import java.util.Random;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.MobConfig;
import com.hbm.entity.mob.ai.EntityAIBreaking;
import com.hbm.entity.pathfinder.PathFinderUtils;
import com.hbm.handler.atmosphere.ChunkAtmosphereManager;
import com.hbm.items.ModItems;
import api.hbm.entity.ISuffocationImmune;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.entity.IRangedAttackMob;

public class EntitySovietSoldier2 extends EntityMob implements IRangedAttackMob, ISuffocationImmune {


    public EntitySovietSoldier2(World world) {
        super(world);
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreaking(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1D, 20, 25, 15.0F));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        //this.tasks.addTask(6, new EntityAI_MLPF(this, EntityPlayer.class, 100, 1D, 16));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
        this.setSize(0.6F, 1.8F);

        this.isImmuneToFire = true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {

        if(source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect)source).getEntity() instanceof EntitySovietSoldier2) {
            return false;
        }

        if(this.getEquipmentInSlot(4) != null && this.getEquipmentInSlot(4).getItem() == Item.getItemFromBlock(Blocks.glass)) {
            if("oxygenSuffocation".equals(source.damageType))
                return false;
            if("thermal".equals(source.damageType))
                return false;
        }

        return super.attackEntityFrom(source, amount);
    }

    protected void entityInit() {
        super.entityInit();
    }

    protected boolean canDespawn() {
        return false;
    }

    protected void addRandomArmor() {
        //super.addRandomArmor();

        this.setCurrentItemOrArmor(0, new ItemStack(ModItems.gun_congolake));

        if(rand.nextInt(5) == 0) {
        }

        if(!ChunkAtmosphereManager.proxy.canBreathe(this)) {
        }
    }

    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        // Clear invalid attack targets (e.g., Stalin or other Soviet soldiers)
        if (this.getAttackTarget() != null &&
                (this.getAttackTarget() instanceof EntityStalin ||
                        this.getAttackTarget() instanceof EntitySovietSoldier ||
                        this.getAttackTarget() instanceof EntitySovietSoldier1 ||
                        this.getAttackTarget() instanceof EntitySovietSoldier2 ||
                        this.getAttackTarget() instanceof EntitySovietOfficer)) {
            this.setAttackTarget(null); // Force target reset
        }

        // Set a new valid target if none exists
        if (this.getAttackTarget() == null) {
            Entity closestPlayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 128.0D);
            if (closestPlayer != null &&
                    !(closestPlayer instanceof EntityStalin) &&
                    !(closestPlayer instanceof EntitySovietSoldier)) {
                this.setAttackTarget((EntityLivingBase) closestPlayer);
            }
        }

        // Proceed with pathfinding if a valid target exists
        if (this.getAttackTarget() != null) {
            this.getNavigator().setPath(PathFinderUtils.getPathEntityToEntityPartial(
                    worldObj, this, this.getAttackTarget(), 50F, true, false, false, true), 1);
        }

    }

    //combat vest = full diamond set
    public int getTotalArmorValue() {
        return 20;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entity, float f) {
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        this.addRandomArmor();
        return super.onSpawnWithEgg(data);
    }

    public boolean isPotionApplicable(PotionEffect potion) {
        if(this.getEquipmentInSlot(4) == null)
            this.setCurrentItemOrArmor(4, new ItemStack(ModItems.gas_mask_m65));

        return false;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(worldObj.isRemote || this.getHealth() <= 0)
            return;


    }
}