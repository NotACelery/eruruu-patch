package dev.maicra.eruruupatch.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record WorldInteractionInfo(
        ResourceLocation id,
        ItemStack leftInput,
        ItemStack rightInput,
        ItemStack output,
        Component description,
        float chance
) {
}
