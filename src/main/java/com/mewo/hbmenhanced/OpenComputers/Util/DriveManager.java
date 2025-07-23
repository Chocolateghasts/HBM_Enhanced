package com.mewo.hbmenhanced.OpenComputers.Util;

import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.server.component.Drive;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DriveManager {
    public static Drive getFirstDrive(Node node) {
        for (Node connectedNode : node.network().nodes()) {
            if (connectedNode.host() instanceof Drive) {
                return (Drive) connectedNode.host();
            }
        }
        return null;
    }

    public static ItemStack getFirstDriveItem(Node node, EnvironmentHost host) {

        if (node == null) {
            return null;
        }
        if (host == null) {
            return null;
        }

        if (!(host instanceof IInventory)) {
            return null;
        }

        IInventory inv = (IInventory) host;
        int size = inv.getSizeInventory();

        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItemDamage() == 7) {
                if (stack.getTagCompound().getBoolean("oc:unmanaged")) {
                    return stack;
                }
            }
        }
        return null;
    }


}
