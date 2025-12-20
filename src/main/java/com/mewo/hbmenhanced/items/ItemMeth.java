package com.mewo.hbmenhanced.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemMeth extends Item {

    public ItemMeth() {
        super();
        this.setUnlocalizedName("hbmenhanced_meth");
        this.setTextureName("hbmenhanced: meth");  // adjust the texture path as needed
        this.setMaxStackSize(16); // for example
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Apply Nausea (Confusion) for e.g. 30 seconds (600 ticks) with amplifier 1
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 600, 1));
            // Apply Strength (Damage Boost) for e.g. 30 seconds (600 ticks) with amplifier 0
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 600, 0));

            // Consume one item from the stack
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }

        return stack;
    }
}
