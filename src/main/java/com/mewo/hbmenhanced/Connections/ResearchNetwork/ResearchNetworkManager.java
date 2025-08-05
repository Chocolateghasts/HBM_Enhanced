package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ControllerTerminalNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.TerminalNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ResearchNetworkManager {
    private static final WeakHashMap<World, Map<NetworkNodeType, AbstractNetwork<?>>> networkMap = new WeakHashMap<>();

    public static AbstractNetwork<?> getNetwork(World world, NetworkNodeType type) {
        Map<NetworkNodeType, AbstractNetwork<?>> map = networkMap.computeIfAbsent(world, w -> new HashMap<>());

        return map.computeIfAbsent(type, t -> createNetworkByType(t));
    }

    public static Map<NetworkNodeType, AbstractNetwork<?>> getNetworks(World world) {
        return networkMap.computeIfAbsent(world, w -> new HashMap<>());
    }

    private static AbstractNetwork<?> createNetworkByType(NetworkNodeType type) {
        switch (type) {
            case RESEARCH:
                return new ResearchNetwork();
            case TERMINAL:
                return new TerminalNetwork(); // create this similarly
            case CONTROLLER_TERMINAL:
                return new ControllerTerminalNetwork(); // also create this
            default:
                throw new IllegalArgumentException("Unknown network type: " + type);
        }
    }

    public static void clear(World world) {
        networkMap.remove(world);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        clear(event.world);
    }

    public static void reset(World world) {
        Map<NetworkNodeType, AbstractNetwork<?>> map = networkMap.get(world);
        if (map == null) return;
        for (AbstractNetwork<?> net : map.values()) {
            net.reset();
        }
    }

}
