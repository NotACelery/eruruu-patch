package dev.maicra.eruruupatch.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

final class CraftingConflictState {
    AbstractContainerMenu menu;
    CraftingContainer craftSlots;
    ResultContainer resultSlots;
    long signature;
    int semanticCandidateCount;
    ResourceLocation selectedRecipeId;
    RecipeHolder<CraftingRecipe> selectedRecipe;

    boolean belongsTo(AbstractContainerMenu expectedMenu) {
        return menu == expectedMenu;
    }

    void reset(
            AbstractContainerMenu menu,
            CraftingContainer craftSlots,
            ResultContainer resultSlots,
            long signature
    ) {
        this.menu = menu;
        this.craftSlots = craftSlots;
        this.resultSlots = resultSlots;
        this.signature = signature;
        this.semanticCandidateCount = 0;
        this.selectedRecipeId = null;
        this.selectedRecipe = null;
    }
}
