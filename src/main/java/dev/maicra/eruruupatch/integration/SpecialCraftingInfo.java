package dev.maicra.eruruupatch.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record SpecialCraftingInfo(
        ResourceLocation id,
        ItemStack input,
        ItemStack output,
        Component description
) {
}
