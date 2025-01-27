package com.mewo.hbmenhanced.OpenComputers;

import com.mewo.hbmenhanced.hbmenhanced;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.mewo.hbmenhanced.OpenComputers.EnvoirementRPComponent;



public class RPComponent implements Item {

    @Override
    public boolean worksWith(ItemStack stack) {
        return stack != null && stack.getItem() == hbmenhanced.researchItem;
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
        return new EnvoirementRPComponent();
    }

    @Override
    public String slot(ItemStack stack) {
        return "card";
    }

    @Override
    public int tier(ItemStack stack) {
        return 2;
    }

    @Override
    public NBTTagCompound dataTag(ItemStack stack) {
        return null;
    }
}
