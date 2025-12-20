package com.mewo.hbmenhanced.recipes;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import com.mewo.hbmenhanced.hbmenhanced;
import java.util.List;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ItemEnums.EnumFuelAdditive;
import com.hbm.items.ItemGenericPart.EnumPartType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.hbm.inventory.OreDictManager.*;
/*

public class ChemicalPlantRecipes {

    public static com.hbm.inventory.recipes.ChemicalPlantRecipes chemicalPlantRecipes = com.hbm.inventory.recipes.ChemicalPlantRecipes.INSTANCE;

    public void registerDefaults() {

        //Fluids

        //Items
        chemicalPlantRecipes.register(new GenericRecipe("chem.meth").setupNamed(20, 400).setIcon(ModItems.gas_full, Fluids.HYDROGEN.getID())
                .inputItems(new RecipesCommon.ComparableStack(ModItems.powder_fire))
                .inputItems(new RecipesCommon.ComparableStack(ModItems.powder_asbestos))
                .inputItems(new RecipesCommon.ComparableStack(ModItems.med_ptsd))
                .inputFluids(new FluidStack(Fluids.HCL, 5_000))
                .inputFluids(new FluidStack(Fluids.AMMONIA, 5_00))
                .inputFluids(new FluidStack(Fluids.BIOFUEL, 1_000))
                .outputItems(new ItemStack(hbmenhanced.meth, 4)));

        //Blocks

    }

    public static HashMap getRecipes() {
        HashMap<Object, Object> recipes = new HashMap<Object, Object>();

        for(GenericRecipe recipe : INSTANCE.recipeOrderedList) {
            List input = new ArrayList();
            if(recipe.inputItem != null) for(RecipesCommon.AStack stack : recipe.inputItem) input.add(stack);
            if(recipe.inputFluid != null) for(FluidStack stack : recipe.inputFluid) input.add(ItemFluidIcon.make(stack));
            List output = new ArrayList();
            if(recipe.outputItem != null) for(GenericRecipes.IOutput stack : recipe.outputItem) output.add(stack.getAllPossibilities());
            if(recipe.outputFluid != null) for(FluidStack stack : recipe.outputFluid) output.add(ItemFluidIcon.make(stack));
            recipes.put(input.toArray(), output.toArray());
        }

        return recipes;
    } 
}
*/