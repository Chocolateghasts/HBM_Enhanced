package com.mewo.hbmenhanced.items;

import com.hbm.items.machine.ItemMachineUpgrade;
import com.mewo.hbmenhanced.hbmenhanced;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemResearchUpgrade extends Item {

    public enum UpgradeType {
        SPEED, POWER, STABILITY
    }

    public final UpgradeType type;
    public final int tier;

    public ItemResearchUpgrade(UpgradeType type, int tier) {
        this.type = type;
        this.tier = tier;
        this.setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }

    public float applySpeed(float current, int tier) {
        System.out.println("Applying speed with tier " + tier);
        switch (tier) {
            case 1: return current * 1.5F;
            case 2: return current * 2;
            case 3: return current * 3;
            default: return current;
        }
    }

    public float applyPower(float current, int tier) {
        System.out.println("Applying power with tier " + tier);
        switch (tier) {
            case 1: return current * 1.5F;
            case 2: return current * 2;
            case 3: return current * 3;
            default: return current;
        }
    }
}
