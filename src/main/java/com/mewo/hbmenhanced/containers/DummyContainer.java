package com.mewo.hbmenhanced.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class DummyContainer extends Container {
    public DummyContainer(InventoryPlayer inventory) {}

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
}
