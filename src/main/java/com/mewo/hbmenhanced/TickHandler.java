package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.Connections.ResearchNetwork.AbstractNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkNodeType;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.NetworkTypes.ResearchNetwork;
import com.mewo.hbmenhanced.Connections.ResearchNetwork.ResearchNetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.Map;


public class TickHandler {

    public TickHandler() {
        System.out.println("Registerd tickhenldar");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (World world : MinecraftServer.getServer().worldServers) {
            if (world == null || world.isRemote) continue;
            if (world.playerEntities.isEmpty()) continue;

            Map<NetworkNodeType, AbstractNetwork<?>> networks = ResearchNetworkManager.getNetworks(world);
            for (AbstractNetwork<?> network : networks.values()) {
                network.update();
            }
        }
    }
}