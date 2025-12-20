package com.mewo.hbmenhanced.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemBallCleaner extends Item {

    public ItemBallCleaner() {
        this.setUnlocalizedName("hbmenhanced_BallCleaner");
        this.setTextureName("hbmenhanced:BallCleaner");
        this.setMaxStackSize(64);
    }
}