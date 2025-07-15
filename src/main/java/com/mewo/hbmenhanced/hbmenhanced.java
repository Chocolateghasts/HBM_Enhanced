package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.Gui.GuiHandler;
import com.mewo.hbmenhanced.OpenComputers.*;
import com.mewo.hbmenhanced.Packets.EnergyPacket;
import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.T1Block;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.Util.getItemValues;
import com.mewo.hbmenhanced.blocks.BlockResearchCore;
import com.mewo.hbmenhanced.blocks.LabBlock;
import com.mewo.hbmenhanced.commands.*;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.items.*;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import li.cil.oc.api.Driver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.*;


import java.util.*;

@Mod(modid = hbmenhanced.MODID, version = hbmenhanced.VERSION)
public class hbmenhanced
{
    public static final String MODID = "hbmenhanced";
    public static final String VERSION = "1.0.0";
    public static SimpleNetworkWrapper network;

    public static final int guiLabBlockID = 0;
    public static final int guiResearchCoreID = 1;
    public static final int guiResearchBlockID = 2;

    public static Item researchPoint;
    public static Item researchItem;
    public static Item linker;
    public static Block labBlock;
    public static Block researchBlock;
    public static Block researchCore;



    @Mod.Instance
    public static hbmenhanced instance;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        getItemValues.init();
    }

    @EventHandler
    public void PreInit(FMLInitializationEvent event) {
        labBlock = new LabBlock(Material.anvil).setBlockName("labBlock");
        researchCore = new BlockResearchCore();
        GameRegistry.registerTileEntity(labBlockTileEntity.class, "labBlock");
        researchItem = new ItemResearchComponent().setUnlocalizedName("Researcher");
        GameRegistry.registerItem(researchItem, "Researcher");
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        GameRegistry.registerBlock(labBlock, "Lab Block").setCreativeTab(tabhbmenhanced);
        GameRegistry.registerBlock(researchCore, "Research Core");
        GameRegistry.registerTileEntity(TileEntityResearchCore.class, "tileEntityResearchCore");
        GameRegistry.registerTileEntity(TileEntityT1.class, "tileEntityT1");
    }



    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("hbmenhanced");
        network.registerMessage(EnergyPacket.Handler.class, EnergyPacket.class, 0, Side.CLIENT);
        Driver.add(new com.mewo.hbmenhanced.OpenComputers.RPComponent());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        getRpValue rpCalculator = new getRpValue();
        rpCalculator.loadHashMap();
        saveRPData.loadRPData();
        linker = new ItemLink().setUnlocalizedName("linker");
        researchPoint = new ItemResearchPoint().setUnlocalizedName("researchPoint");
        GameRegistry.registerItem(linker, "Linker");
        GameRegistry.registerItem(researchPoint, "Research Point");
        GameRegistry.registerTileEntity(labBlockTileEntity.class, "labBlockTileEntity");
        researchBlock = new T1Block().setBlockName("researchBlockT1");
        GameRegistry.registerBlock(researchBlock, "researchBlockT1");

//        researchBlock = new ResearchBlock(1, "researchBlock").setBlockName("researchBlock");
//        GameRegistry.registerBlock(researchBlock, "researchBlock");
//        GameRegistry.registerTileEntity(TileEntityResearchBlock.class, "TileEntityResearchBlock");
    }




    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {

        ResearchTree tree = new ResearchTree(event.getServer());
        tree.getNodes();
        Map<String, Boolean> testmap = new HashMap<>();
        testmap.put("test", true);
        List<Map<String, Object>> templates = tree.createTemplates(
                "A", 1,
                "B", 2,
                "C", 5,
                "A", 9
        );

        //tree.editNode("node_1", null, null, null, null, null, templates, null, null, testmap, 30.0f, 30.0f, getRpValue.researchType.STRUCTURAL);
        //tree.editNode("node_0", null, null, null, null, null, templates, null, null, testmap, 10.0f, 10.0f, getRpValue.researchType.NUCLEAR);

        event.registerServerCommand(new RPCommand());
        event.registerServerCommand(new showRPCommand());
        event.registerServerCommand(new TeamCommand());
        getRpValue.setServer(event.getServer());

        PointManager manager = new PointManager();
        manager.createFile(event.getServer().getEntityWorld());

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Result res = PointManager.saveData();
                System.out.println(res.isSuccess() + res.getMessage());
            }
        }, 0, 60000);
    }

    public static CreativeTabs tabhbmenhanced = new CreativeTabs("tabhbmenhanced") {
        @Override
        public Item getTabIconItem() {
            return new ItemStack(researchPoint).getItem();
        }
    };
}
