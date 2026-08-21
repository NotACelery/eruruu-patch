package dev.maicra.eruruupatch.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public record CraftingRecipeCandidate(
        RecipeHolder<CraftingRecipe> recipe,
        ItemStack output,
        NonNullList<ItemStack> remainders
) {
    public ResourceLocation id() {
        return recipe.id();
    }
}
