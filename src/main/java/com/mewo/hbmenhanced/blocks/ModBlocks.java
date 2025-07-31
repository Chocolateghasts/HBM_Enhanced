package com.mewo.hbmenhanced.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {

    // Custom Renders
    public static final Block researchCable;


    public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static final Map<String, Class<? extends TileEntity>> TILE_ENTITIES = new LinkedHashMap<>();

    static {
        // Add your blocks here
        researchCable = addBlock(new com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.BlockResearchCable());
        addBlock(new com.mewo.hbmenhanced.ResearchBlocks.ResearchSource.BlockResearchSource());
        addBlock(new com.mewo.hbmenhanced.ResearchBlocks.ResearchTerminal.BlockResearchTerminal());



        // Add tile entities here
        addTileEntity(com.mewo.hbmenhanced.ResearchBlocks.ResearchSource.TileEntityResearchSource.class);
        addTileEntity(com.mewo.hbmenhanced.ResearchBlocks.ResearchCable.TileEntityResearchCable.class);
        addTileEntity(com.mewo.hbmenhanced.ResearchBlocks.ResearchTerminal.TileEntityResearchTerminal.class);
    }

    private static Block addBlock(Block block) {
        String name = block.getClass().getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1); // camelCase
        block.setBlockName(name);
        BLOCKS.put(name, block);
        return block;
    }

    private static void addTileEntity(Class<? extends TileEntity> clazz) {
        String name = clazz.getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1); // camelCase
        TILE_ENTITIES.put(name, clazz);
    }

    public static void register() {
        for (Map.Entry<String, Block> entry : BLOCKS.entrySet()) {
            GameRegistry.registerBlock(entry.getValue(), entry.getKey());
        }

        for (Map.Entry<String, Class<? extends TileEntity>> entry : TILE_ENTITIES.entrySet()) {
            GameRegistry.registerTileEntity(entry.getValue(), entry.getKey());
        }
    }
}
