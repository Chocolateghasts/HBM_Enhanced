package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.commands.RPCommand;
import com.mewo.hbmenhanced.commands.showRPCommand;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = hbmenhanced.MODID, version = hbmenhanced.VERSION)
public class hbmenhanced
{
    public static final String MODID = "hbmenhanced";
    public static final String VERSION = "1.0.0";

    public static Block labBlock;
    @EventHandler
    public void PreInit(FMLInitializationEvent event) {
        labBlock = new LabBlock(Material.anvil).setBlockName("labBlock");
        GameRegistry.registerBlock(labBlock, "Lab Block");
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Register your custom command
        event.registerServerCommand(new RPCommand());
        event.registerServerCommand(new showRPCommand());
    }
}
