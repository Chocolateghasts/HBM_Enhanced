package com.mewo.hbmenhanced.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemBlueMeth extends Item {

    public ItemBlueMeth() {
        super();
        this.setUnlocalizedName("hbmenhanced_bluemeth");
        this.setTextureName("hbmenhanced:bluemeth");  // adjust your texture path
        this.setMaxStackSize(16);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Example: give nausea + strength, but different values than normal meth
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 400, 1));  // ~20 sec
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 400, 1)); // strength level 2

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }
}
