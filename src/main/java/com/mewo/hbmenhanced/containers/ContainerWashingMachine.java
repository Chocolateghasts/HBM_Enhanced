package com.mewo.hbmenhanced.containers;

import com.mewo.hbmenhanced.blocks.tileentity.TileEntityWashingMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerWashingMachine extends Container {

    private TileEntityWashingMachine tile;

    public ContainerWashingMachine(InventoryPlayer player, TileEntityWashingMachine tile) {
        this.tile = tile;

        // 3x3 machine inventory
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new Slot(tile, slot++, 62 + x * 18, 17 + y * 18));
            }
        }

        // Player inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Hotbar
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }
}
