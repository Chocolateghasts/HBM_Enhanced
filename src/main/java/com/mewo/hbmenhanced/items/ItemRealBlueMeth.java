package com.mewo.hbmenhanced.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemRealBlueMeth extends Item {

    public ItemRealBlueMeth() {
        super();
        this.setUnlocalizedName("hbmenhanced_realbluemeth");
        this.setTextureName("hbmenhanced:realbluemeth");  // adjust your texture path
        this.setMaxStackSize(8); // maybe rarer so fewer stack
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // More powerful version: stronger effects, maybe longer duration
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 600, 2));     // ~30 sec, amp 3
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 600, 2));   // strength level 3

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }
}
