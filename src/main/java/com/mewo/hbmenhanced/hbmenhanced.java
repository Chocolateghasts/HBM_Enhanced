package com.mewo.hbmenhanced;

//import com.mewo.hbmenhanced.OpenComputers.RPComponentProvider;
import com.mewo.hbmenhanced.Gui.labBlockGuiHandler;
import com.mewo.hbmenhanced.blocks.LabBlock;
import com.mewo.hbmenhanced.commands.RPCommand;
import com.mewo.hbmenhanced.commands.showRPCommand;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.items.ItemResearchComponent;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import li.cil.oc.api.Driver;
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

    public static final int guiLabBlockID = 0;

    public static Item researchPoint;
    public static Item researchItem;
    public static Block labBlock;



    @Mod.Instance
    public static hbmenhanced instance;

    @EventHandler
    public void PreInit(FMLInitializationEvent event) {
        labBlock = new LabBlock(Material.anvil).setBlockName("labBlock");
        GameRegistry.registerTileEntity(labBlockTileEntity.class, "labBlock");
        researchItem = new ItemResearchComponent().setUnlocalizedName("Researcher");
        GameRegistry.registerItem(researchItem, "Researcher");
        //GameRegistry.registerItem(ItemResearchComponent.class, "Researcher");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new labBlockGuiHandler());
        GameRegistry.registerBlock(labBlock, "Lab Block").setCreativeTab(tabhbmenhanced);
    }



    @EventHandler
    public void init(FMLInitializationEvent event)
    {

        Driver.add(new com.mewo.hbmenhanced.OpenComputers.RPComponent());
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        getRpValue rpCalculator = new getRpValue();
        rpCalculator.loadHashMap();
        saveRPData.loadRPData();
        researchPoint = new ItemResearchPoint().setUnlocalizedName("researchPoint");
        GameRegistry.registerItem(researchPoint, "Research Point");
        GameRegistry.registerTileEntity(labBlockTileEntity.class, "labBlockTileEntity");
    }


    //GameRegistry.addSmelting(oreIronOre, new ItemStack(itemIronIngot), 0);

    //GameRegistry.registerFuelHandler(new FuelHandler());




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
            return new ItemStack(researchPoint).getItem();
        }
    };
}
