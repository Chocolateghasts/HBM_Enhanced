package com.mewo.hbmenhanced.Connections.ResearchNetwork;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class ResearchNetworkManager {
    private static final WeakHashMap<World, ResearchNetwork> networkMap = new WeakHashMap<>();
    public static ResearchNetwork get(World world) {
        return networkMap.computeIfAbsent(world, w -> new ResearchNetwork());
    }
    public static void clear(World world) {
        networkMap.remove(world);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        clear(event.world);
    }

    public static void reset() {
        for (Map.Entry<World, ResearchNetwork> entry : networkMap.entrySet()) {
            World world = entry.getKey();
            ResearchNetwork net = entry.getValue();
            net.reset();
        }
    }

}
