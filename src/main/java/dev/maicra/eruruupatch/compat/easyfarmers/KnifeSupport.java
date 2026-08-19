package dev.maicra.eruruupatch.compat.easyfarmers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** @deprecated Use FarmerToolSupport. Kept as a source/binary migration facade for the sandbox. */
@Deprecated(forRemoval = false)
public final class KnifeSupport {
    public static final TagKey<Item> KNIVES = FarmerToolSupport.KNIVES;
    public static final ResourceLocation EMPTY_KNIFE_SLOT = FarmerToolSupport.EMPTY_KNIFE_SLOT;

    private KnifeSupport() {
    }

    public static boolean isKnife(ItemStack stack) {
        return FarmerToolSupport.isKnife(stack);
    }
}
