package com.pahimar.ee3.recipe;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pahimar.ee3.exchange.WrappedStack;
import com.pahimar.ee3.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeRegistry
{
    private static RecipeRegistry recipeRegistry = null;

    private Multimap<WrappedStack, List<WrappedStack>> recipeMap;
    private ImmutableMultimap<WrappedStack, List<WrappedStack>> immutableRecipeMap;

    private RecipeRegistry()
    {
        recipeMap = HashMultimap.create(); // TODO Switch this to a TreeMultimap
    }

    public static RecipeRegistry getInstance()
    {
        if (recipeRegistry == null)
        {
            recipeRegistry = new RecipeRegistry();
        }

        return recipeRegistry;
    }

    public void addRecipe(Object recipeOutput, List<?> recipeInputList)
    {
        // Wrap the recipe output
        WrappedStack wrappedRecipeOutput = WrappedStack.wrap(recipeOutput);
        if (wrappedRecipeOutput == null)
        {
            return;
        }

        List<WrappedStack> wrappedRecipeInputList = new ArrayList<WrappedStack>();
        for (Object recipeInputObject : recipeInputList)
        {
            WrappedStack wrappedInputObject = WrappedStack.wrap(recipeInputObject);
            if (wrappedInputObject != null)
            {
                wrappedRecipeInputList.add(wrappedInputObject);
            }
            else
            {
                return;
            }
        }

        // Add the recipe mapping only if we don't already have it
        if (!recipeMap.get(wrappedRecipeOutput).contains(wrappedRecipeInputList))
        {
            recipeMap.put(wrappedRecipeOutput, wrappedRecipeInputList);
        }
    }

    public void registerVanillaRecipes()
    {
        RecipesVanilla.registerRecipes();
        RecipesFluidContainers.registerRecipes();
        RecipesPotions.registerRecipes();
    }

    public Multimap<WrappedStack, List<WrappedStack>> getRecipeMappings()
    {
        if (immutableRecipeMap == null)
        {
            immutableRecipeMap = ImmutableMultimap.copyOf(recipeRegistry.recipeMap);
        }

        return immutableRecipeMap;
    }

    public void dumpRecipeRegistryToLog()
    {
        for (WrappedStack wrappedStack : getRecipeMappings().keySet())
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("Output: %s, Inputs: ", wrappedStack.toString()));
            for (List<WrappedStack> listStacks : getRecipeMappings().get(wrappedStack))
            {
                for (WrappedStack listStack : listStacks)
                {
                    stringBuilder.append(listStack.toString() + " ");
                }
            }
            LogHelper.info(stringBuilder.toString());
        }
    }
}