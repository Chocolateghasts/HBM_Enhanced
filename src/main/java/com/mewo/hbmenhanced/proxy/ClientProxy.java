package com.mewo.hbmenhanced.proxy;

import com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.BlockResearchCable;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.CableRenderer;
import com.mewo.hbmenhanced.blocks.ModBlocks;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers() {
        BlockResearchCable.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CableRenderer());
    }
}
