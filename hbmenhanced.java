package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.commands.RPCommand;
import com.mewo.hbmenhanced.commands.showRPCommand;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = hbmenhanced.MODID, version = hbmenhanced.VERSION)
public class hbmenhanced
{
    public static final String MODID = "hbmenhanced";
    public static final String VERSION = "1.0.0";

    public static Block labBlock;
    @EventHandler
    public void PreInit(FMLInitializationEvent event) {
        labBlock = new LabBlock(Material.anvil).setBlockName("labBlock");
        GameRegistry.registerBlock(labBlock, "Lab Block").setCreativeTab(tabhbmenhanced);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        saveRPData.loadRPData();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Register your custom command
        event.registerServerCommand(new RPCommand());
        event.registerServerCommand(new showRPCommand());

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    saveRPData.saveRPData();  // Periodically save RP data
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 60000);  // Save every 60 seconds
    }

    public static CreativeTabs tabhbmenhanced = new CreativeTabs("tabhbmenhanced") {
        @Override
        public Item getTabIconItem() {
            return new ItemStack(labBlock).getItem();
        }
    };
}
