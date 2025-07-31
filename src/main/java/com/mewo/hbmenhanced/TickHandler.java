package com.mewo.hbmenhanced;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import static com.mewo.hbmenhanced.hbmenhanced.RESEARCH_NETWORK;

public class TickHandler {

    public TickHandler() {
        System.out.println("Registerd tickhenldar");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RESEARCH_NETWORK.update();
        }
    }
}
