package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.commands.RPCommand;
import com.mewo.hbmenhanced.commands.showRPCommand;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = hbmenhanced.MODID, version = hbmenhanced.VERSION)
public class hbmenhanced
{
    public static final String MODID = "hbmenhanced";
    public static final String VERSION = "1.0.0";
    
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
}
