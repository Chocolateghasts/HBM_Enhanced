package com.mewo.hbmenhanced.OpenComputers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;

import com.mewo.hbmenhanced.commands.RPCommand;

import java.util.HashMap;
import java.util.Map;

public class EnvoirementRPComponent implements ManagedEnvironment {

    private Node node; // Declare the node variable

    public EnvoirementRPComponent() {
        node = li.cil.oc.api.Network.newNode(this, Visibility.Neighbors)
                .withComponent("RPComponent", Visibility.Neighbors)
                .create();
    }

    @Callback
    public Object[] getPlayerRP(Context context, Arguments args) {
        Map<String, Integer> rpMap = RPCommand.playerRPMap;

        HashMap<Object, Object> luaTable = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rpMap.entrySet()) {
            luaTable.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{luaTable};
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void load(NBTTagCompound nbt) {
        if (node != null) {
            node.load(nbt);
        }
    }

    @Override
    public void save(NBTTagCompound nbt) {
        if (node != null) {
            node.save(nbt);
        }
    }

    @Override
    public Node node() {
        return node; // Return the node
    }

    @Override
    public void onConnect(Node node) {
    }

    @Override
    public void onDisconnect(Node node) {
    }

    @Override
    public void onMessage(Message message) {
    }
}
