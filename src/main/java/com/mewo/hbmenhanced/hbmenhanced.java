package com.mewo.hbmenhanced;

import com.mewo.hbmenhanced.Gui.GuiHandler;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.OpenComputers.RpComponentDriver;
import com.mewo.hbmenhanced.Packets.*;
import com.mewo.hbmenhanced.ReactorResearch.TileEntityResearchCore;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.BlockResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.ResearchController.TileEntityResearchController;
import com.mewo.hbmenhanced.ResearchBlocks.Test;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.T1Block;
import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.T2Block;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.T3Block;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.Util.getItemValues;
import com.mewo.hbmenhanced.blocks.BlockResearchCore;
import com.mewo.hbmenhanced.blocks.LabBlock;
import com.mewo.hbmenhanced.blocks.ModBlocks;
import com.mewo.hbmenhanced.commands.*;
import com.mewo.hbmenhanced.containers.labBlockTileEntity;
import com.mewo.hbmenhanced.items.*;
import com.mewo.hbmenhanced.proxy.CommonProxy;
import com.mewo.hbmenhanced.recipes.ServerTemplates;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = hbmenhanced.MODID, version = hbmenhanced.VERSION)
public class hbmenhanced
{
    public static final String MODID = "hbmenhanced";
    public static final String VERSION = "1.0.0";
    public static SimpleNetworkWrapper network;

    public static final Logger LOGGER = LogManager.getLogger(hbmenhanced.class);

    public static final int guiLabBlockID = 0;
    public static final int guiResearchCoreID = 1;
    public static final int guiResearchBlockID = 2;
    public static final int guiResearchBlockT2ID = 3;
    public static final int guiResearchBlockT3ID = 4;
    public static final int guiResearchControllerID = 5;
    public static final int guiResearchTerminalID = 6;

    public static Item researchPoint;
    public static Item researchItem;
    public static Item linker;

    public static Block labBlock;
    public static Block researchBlock;
    public static Block researchCore;
    public static Block researchBlockT2;
    public static Block researchBlockT3;
    public static Block researchController;

    @SidedProxy(
            clientSide = "com.mewo.hbmenhanced.proxy.ClientProxy",
            serverSide = "com.mewo.hbmenhanced.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

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
        GameRegistry.registerTileEntity(TileEntityT2.class, "tileEntityT2");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ServerTemplates.preInit();
        proxy.registerRenderers();
        FMLCommonHandler.instance().bus().register(new TickHandler());
        FMLCommonHandler.instance().bus().register(new ServerEventHandler());
        Test testInstance = new Test();
        FMLCommonHandler.instance().bus().register(testInstance);
        MinecraftForge.EVENT_BUS.register(testInstance);
        network = NetworkRegistry.INSTANCE.newSimpleChannel("hbmenhanced");
        int packetId = 0;
        network.registerMessage(EnergyPacket.Handler.class, EnergyPacket.class, packetId++, Side.CLIENT);
        network.registerMessage(ConnectionsPacket.Handler.class, ConnectionsPacket.class, packetId++, Side.CLIENT);

        // PacketResearchTree is bidirectional, so register for both server and client
        network.registerMessage(PacketResearchTree.Handler.class, PacketResearchTree.class, packetId++, Side.SERVER);
        network.registerMessage(PacketResearchTree.Handler.class, PacketResearchTree.class, packetId++, Side.CLIENT);

        // PacketResearchTreeRequest only sent client -> server
        network.registerMessage(PacketResearchTreeRequest.Handler.class, PacketResearchTreeRequest.class, packetId++, Side.SERVER);

        // PacketResearchTreeResponse only sent server -> client
        network.registerMessage(PacketResearchTreeResponse.Handler.class, PacketResearchTreeResponse.class, packetId++, Side.CLIENT);
        network.registerMessage(PacketSyncTeam.Handler.class, PacketSyncTeam.class, packetId++, Side.CLIENT);

        network.registerMessage(PacketTemplates.Handler.class, PacketTemplates.class, packetId++, Side.CLIENT);
        Driver.add(new RpComponentDriver());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModItems.init();
        ModItems.register();
        getRpValue rpCalculator = new getRpValue();
        rpCalculator.loadHashMap();
        saveRPData.loadRPData();
        ModBlocks.register();
        linker = new ItemLink().setUnlocalizedName("linker");
        researchPoint = new ItemResearchPoint().setUnlocalizedName("researchPoint");
        GameRegistry.registerItem(linker, "Linker");
        GameRegistry.registerItem(researchPoint, "Research Point");
        GameRegistry.registerTileEntity(labBlockTileEntity.class, "labBlockTileEntity");
        researchBlock = new T1Block().setBlockName("researchBlockT1");
        GameRegistry.registerBlock(researchBlock, "researchBlockT1");
        researchBlockT2 = new T2Block().setBlockName("researchBlockT2");
        GameRegistry.registerBlock(researchBlockT2, "researchBlockT2");
        researchBlockT3 = new T3Block().setBlockName("researchBlockT3");
        GameRegistry.registerBlock(researchBlockT3, "researchBlockT3");
        GameRegistry.registerTileEntity(TileEntityT3.class, "tileEntityT3");
        researchController = new BlockResearchController().setBlockName("blockResearchController");
        GameRegistry.registerBlock(researchController, "blockResearchController");
        GameRegistry.registerTileEntity(TileEntityResearchController.class, "tileEntityResearchController");
    }




    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //ResearchNetworkManager.reset();
        MinecraftServer server = event.getServer();
        ResearchTree.init(server);
        //ResearchTree adminTree = new ResearchTree("test");
//        adminTree.save();

//        ResearchTreeold tree = new ResearchTreeold(event.getServer());
//        tree.getNodes();
//        Map<String, Boolean> testmap = new HashMap<>();
//        testmap.put("test", true);
//        List<Map<String, Object>> templates = tree.createTemplates(
//                "A", 1,
//                "B", 2,
//                "C", 5,
//                "A", 9
//        );

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
//                for (Object player : server.getEntityWorld().playerEntities) {
//                    if (player instanceof EntityPlayerMP) {
//                        EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
//                        String teamName = mpPlayer.getEntityData().getString("hbmenhanced:team");
//                        System.out.println("SERVER TEAM FOR PLR: " + teamName);
//                        network.sendTo(new PacketSyncTeam(teamName), mpPlayer);
//                    }
//                }
            }
        }, 0, 60000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Result res = PointManager.saveData();
                System.out.println(res.isSuccess() + res.getMessage());
                for (Object player : server.getEntityWorld().playerEntities) {
                    if (player instanceof EntityPlayerMP) {
                        EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
                        String teamName = mpPlayer.getEntityData().getString("hbmenhanced:team");
                        System.out.println("SERVER TEAM FOR PLR: " + teamName);
                        network.sendTo(new PacketSyncTeam(teamName), mpPlayer);
                    }
                }
            }
        }, 0, 10000);

        ServerTemplates.init();
    }

    public static CreativeTabs tabhbmenhanced = new CreativeTabs("tabhbmenhanced") {
        @Override
        public Item getTabIconItem() {
            return new ItemStack(researchPoint).getItem();
        }
    };
}
