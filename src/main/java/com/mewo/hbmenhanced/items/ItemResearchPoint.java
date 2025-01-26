package com.mewo.hbmenhanced.items;

import com.mewo.hbmenhanced.hbmenhanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import static com.mewo.hbmenhanced.hbmenhanced.tabhbmenhanced;

public class ItemResearchPoint extends Item {
    public ItemResearchPoint() {
        this.setCreativeTab(tabhbmenhanced);
    }

    @SideOnly(Side.CLIENT)
    public void registerItemIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(hbmenhanced.MODID + ":" + this.getUnlocalizedName().substring(5));
    }
}