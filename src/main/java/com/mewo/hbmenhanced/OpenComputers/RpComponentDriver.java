package com.mewo.hbmenhanced.OpenComputers;

import com.mewo.hbmenhanced.hbmenhanced;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.item.ItemStack;

public class RpComponentDriver extends DriverItem {

    public RpComponentDriver() {
        super(new ItemStack(hbmenhanced.researchItem));
    }
    @Override
    public ManagedEnvironment createEnvironment(ItemStack itemStack, EnvironmentHost environmentHost) {
        return new EnvRpComponent(itemStack, environmentHost);
    }

    @Override
    public String slot(ItemStack itemStack) {
        return "card";
    }

    @Override
    public boolean worksWith(ItemStack stack) {
        return stack != null && stack.getItem() == hbmenhanced.researchItem;
    }

    @Override
    public int tier(ItemStack stack) {
        return 1;
    }
}