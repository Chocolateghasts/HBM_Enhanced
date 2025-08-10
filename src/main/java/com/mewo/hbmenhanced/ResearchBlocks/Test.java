package com.mewo.hbmenhanced.ResearchBlocks;

import com.hbm.inventory.gui.GUIMachineAssemblyMachine;
import com.hbm.inventory.gui.GUIMachineChemicalFactory;
import com.hbm.inventory.gui.GUIMachineChemicalPlant;
import com.hbm.inventory.gui.GUIScreenRecipeSelector;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.tileentity.machine.TileEntityMachineAssemblyMachine;
import com.hbm.tileentity.machine.TileEntityMachineChemicalFactory;
import com.hbm.tileentity.machine.TileEntityMachineChemicalPlant;
import com.mewo.hbmenhanced.Util.ResearchTemplate;
import com.mewo.hbmenhanced.recipes.ClientTemplateSync;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.GuiOpenEvent;
import scala.tools.nsc.doc.base.comment.Bold;

import java.lang.reflect.Field;
import java.util.*;

public class Test {

    private final Minecraft mc = Minecraft.getMinecraft();

    private TileEntityMachineAssemblyMachine assemblyMachine;

    private boolean selectorOpened = false;
    private boolean shouldUpdateAssembler = false;

    Map<GuiScreen, Boolean> shouldUpdate = new HashMap<>();
    private GuiScreen lastMachineGui = null;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GUIScreenRecipeSelector && !selectorOpened) {
            selectorOpened = true;
            TileEntity tile = getTileEntity(lastMachineGui);
            if (tile == null) {
                event.gui = null;
                return;
            }
            shouldUpdate.put(lastMachineGui, true);
        } else {
            selectorOpened = false;
            if (event.gui instanceof GUIMachineAssemblyMachine ||
                    event.gui instanceof GUIMachineChemicalPlant ||
                    event.gui instanceof GUIMachineChemicalFactory) {
                lastMachineGui = event.gui;
            } else {
                lastMachineGui = null;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (Map.Entry<GuiScreen, Boolean> entry : shouldUpdate.entrySet()) {
            GuiScreen gui = entry.getKey();
            Boolean update = entry.getValue();
            if (!update) continue;

            TileEntity tile = getTileEntity(gui);
            if (tile == null) continue;

            mc.currentScreen = null;
            shouldUpdate.put(gui, false);

            if (tile instanceof TileEntityMachineAssemblyMachine) {
                AssemblyMachineRecipes recipes = getFilteredAssemblyMachineRecipes();
                GUIScreenRecipeSelector.openSelector(recipes, (TileEntityMachineAssemblyMachine) tile, ((TileEntityMachineAssemblyMachine) tile).assemblerModule.recipe, 0, ItemBlueprints.grabPool(((TileEntityMachineAssemblyMachine) tile).slots[1]), mc.currentScreen);
            } else if (tile instanceof TileEntityMachineChemicalPlant) {
                ChemicalPlantRecipes recipes = getFilteredChemicalPlantRecipes();
                GUIScreenRecipeSelector.openSelector(recipes, (TileEntityMachineChemicalPlant) tile, ((TileEntityMachineChemicalPlant) tile).chemplantModule.recipe, 0, null, mc.currentScreen);
            } else if (tile instanceof TileEntityMachineChemicalFactory) {
                ChemicalPlantRecipes recipes = getFilteredChemicalPlantRecipes();
                GUIScreenRecipeSelector.openSelector(recipes, (TileEntityMachineChemicalFactory) tile, Arrays.toString(((TileEntityMachineChemicalFactory) tile).chemplantModule), 0, null, mc.currentScreen);
            }
        }


        if (shouldUpdateAssembler) {
            mc.currentScreen = null;
            shouldUpdateAssembler = false;
            AssemblyMachineRecipes filtered = getFilteredAssemblyMachineRecipes();
            GUIScreenRecipeSelector.openSelector(filtered, assemblyMachine, assemblyMachine.assemblerModule.recipe, 0, ItemBlueprints.grabPool(assemblyMachine.slots[1]), mc.currentScreen);
        }
        GuiScreen currentGui = mc.currentScreen;
        if (!(currentGui instanceof GUIMachineAssemblyMachine)) return;




//        try {
//            GUIMachineAssemblyMachine gui = (GUIMachineAssemblyMachine) currentGui;
//            Field assemblerField = GUIMachineAssemblyMachine.class.getDeclaredField("assembler");
//            assemblerField.setAccessible(true);
//            TileEntityMachineAssemblyMachine assembler = (TileEntityMachineAssemblyMachine) assemblerField.get(gui);
//            assemblyMachine = assembler;
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }


    public TileEntity getTileEntity(GuiScreen guiScreen) {
        TileEntity tileEntity = null;
        try {
            if (guiScreen instanceof GUIMachineAssemblyMachine) {
                GUIMachineAssemblyMachine gui = (GUIMachineAssemblyMachine) guiScreen;
                Field te = GUIMachineAssemblyMachine.class.getDeclaredField("assembler");
                te.setAccessible(true);
                tileEntity = (TileEntityMachineAssemblyMachine) te.get(gui);
                return tileEntity;
            } else if (guiScreen instanceof GUIMachineChemicalPlant) {
                GUIMachineChemicalPlant gui = (GUIMachineChemicalPlant) guiScreen;
                Field te = GUIMachineChemicalPlant.class.getDeclaredField("chemplant");
                te.setAccessible(true);
                tileEntity = (TileEntityMachineChemicalPlant) te.get(gui);
                return tileEntity;
            } else if (guiScreen instanceof GUIMachineChemicalFactory) {
                GUIMachineChemicalFactory gui = (GUIMachineChemicalFactory) guiScreen;
                Field te = GUIMachineChemicalFactory.class.getDeclaredField("chemplant");
                te.setAccessible(true);
                tileEntity = (TileEntityMachineChemicalFactory) te.get(gui);
                return tileEntity;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return tileEntity;
    }

    private AssemblyMachineRecipes getFilteredAssemblyMachineRecipes() {
        Set<ResearchTemplate> unlocked = new HashSet<>(ClientTemplateSync.getAssemblyTemplates());
        AssemblyMachineRecipes filtered = new AssemblyMachineRecipes();

        for (GenericRecipe recipe : AssemblyMachineRecipes.INSTANCE.recipeOrderedList) {
            ResearchTemplate template = new ResearchTemplate("a", recipe.getInternalName());
            if (unlocked.contains(template)) {
                filtered.register(recipe);
            }
        }
        System.out.println("FILTERED: " + Arrays.toString(filtered.recipeOrderedList.toArray()));
        return filtered;
    }


    private ChemicalPlantRecipes getFilteredChemicalPlantRecipes() {
        Set<ResearchTemplate> unlocked = new HashSet<>(ClientTemplateSync.getChemicalTemplates());
        ChemicalPlantRecipes filtered = new ChemicalPlantRecipes();

        for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
            ResearchTemplate template = new ResearchTemplate("b", recipe.getInternalName());
            if (unlocked.contains(template)) {
                filtered.register(recipe);
            }
        }
        System.out.println("FILTERED: " + Arrays.toString(filtered.recipeOrderedList.toArray()));
        return filtered;
    }

    private ChemicalPlantRecipes getFilteredChemicalFactoryRecipes() {
        Set<ResearchTemplate> unlocked = new HashSet<>(ClientTemplateSync.getChemicalTemplates());
        ChemicalPlantRecipes filtered = new ChemicalPlantRecipes();

        for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
            ResearchTemplate template = new ResearchTemplate("b", recipe.getInternalName());
            if (unlocked.contains(template)) {
                filtered.register(recipe);
            }
        }
        System.out.println("FILTERED: " + Arrays.toString(filtered.recipeOrderedList.toArray()));
        return filtered;
    }
}
