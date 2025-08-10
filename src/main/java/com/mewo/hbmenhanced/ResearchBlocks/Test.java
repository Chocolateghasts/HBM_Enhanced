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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.lang.reflect.Field;

import java.util.*;

public class Test {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Changed to Set to track which GUIs need update
    private final Set<GuiScreen> shouldUpdate = Collections.newSetFromMap(new WeakHashMap<>());

    private GuiScreen lastMachineGui = null;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GUIScreenRecipeSelector && !shouldUpdate.isEmpty()) {
            // Opened recipe selector, mark last GUI for update if tile exists
            TileEntity tile = getTileEntity(lastMachineGui);
            if (tile == null) {
                event.gui = null;
                return;
            }
            shouldUpdate.add(lastMachineGui);
        } else {
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

        Iterator<GuiScreen> iterator = shouldUpdate.iterator();
        while (iterator.hasNext()) {
            GuiScreen gui = iterator.next();
            TileEntity tile = getTileEntity(gui);
            if (tile == null) {
                iterator.remove();
                continue;
            }

            mc.currentScreen = null; // Close current GUI

            // Remove from set since we are processing it now
            iterator.remove();

            if (tile instanceof TileEntityMachineAssemblyMachine) {
                AssemblyMachineRecipes recipes = getFilteredAssemblyMachineRecipes();
                GUIScreenRecipeSelector.openSelector(recipes,
                        (TileEntityMachineAssemblyMachine) tile,
                        ((TileEntityMachineAssemblyMachine) tile).assemblerModule.recipe,
                        0,
                        ItemBlueprints.grabPool(((TileEntityMachineAssemblyMachine) tile).slots[1]),
                        mc.currentScreen);
            } else if (tile instanceof TileEntityMachineChemicalPlant) {
                ChemicalPlantRecipes recipes = getFilteredChemicalPlantRecipes();
                GUIScreenRecipeSelector.openSelector(recipes,
                        (TileEntityMachineChemicalPlant) tile,
                        ((TileEntityMachineChemicalPlant) tile).chemplantModule.recipe,
                        0,
                        null,
                        mc.currentScreen);
            } else if (tile instanceof TileEntityMachineChemicalFactory) {
                ChemicalPlantRecipes recipes = getFilteredChemicalFactoryRecipes();
                GUIScreenRecipeSelector.openSelector(recipes,
                        (TileEntityMachineChemicalFactory) tile,
                        Arrays.toString(((TileEntityMachineChemicalFactory) tile).chemplantModule),
                        0,
                        null,
                        mc.currentScreen);
            }
        }
    }

    public TileEntity getTileEntity(GuiScreen guiScreen) {
        if (guiScreen == null) return null;

        try {
            if (guiScreen instanceof GUIMachineAssemblyMachine) {
                Field te = GUIMachineAssemblyMachine.class.getDeclaredField("assembler");
                te.setAccessible(true);
                return (TileEntityMachineAssemblyMachine) te.get(guiScreen);
            } else if (guiScreen instanceof GUIMachineChemicalPlant) {
                Field te = GUIMachineChemicalPlant.class.getDeclaredField("chemplant");
                te.setAccessible(true);
                return (TileEntityMachineChemicalPlant) te.get(guiScreen);
            } else if (guiScreen instanceof GUIMachineChemicalFactory) {
                Field te = GUIMachineChemicalFactory.class.getDeclaredField("chemplant");
                te.setAccessible(true);
                return (TileEntityMachineChemicalFactory) te.get(guiScreen);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AssemblyMachineRecipes getFilteredAssemblyMachineRecipes() {
        AssemblyMachineRecipes filtered = new AssemblyMachineRecipes();
        for (GenericRecipe recipe : AssemblyMachineRecipes.INSTANCE.recipeOrderedList) {
            if (recipe.getInternalName().contains("missile")) {
                filtered.register(recipe);
            }
        }
        return filtered;
    }

    private ChemicalPlantRecipes getFilteredChemicalPlantRecipes() {
        ChemicalPlantRecipes filtered = new ChemicalPlantRecipes();
        for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
            if (recipe.getInternalName().contains("concrete")) {
                filtered.register(recipe);
            }
        }
        return filtered;
    }

    // Add this for Chemical Factory filtering similar to above
    private ChemicalPlantRecipes getFilteredChemicalFactoryRecipes() {
        ChemicalPlantRecipes filtered = new ChemicalPlantRecipes();
        for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
            // Example filter, replace with actual criteria for chemical factory
            if (recipe.getInternalName().contains("factory")) {
                filtered.register(recipe);
            }
        }
        return filtered;
    }
}
