package dev.maicra.eruruupatch.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/** Viewer-only description of tool-aware harvesting performed by Rich Farmers. */
public record FarmerHarvestInfo(
        ResourceLocation id,
        ItemStack input,
        Ingredient tool,
        List<ItemStack> outputs,
        Component description
) {
}
