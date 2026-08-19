package dev.maicra.eruruupatch.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Viewer-only representation of one Cutter work-surface material variant. */
public record CutterVariantInfo(
        ResourceLocation id,
        ItemStack material,
        ItemStack cutter,
        Component variantName
) {
}
