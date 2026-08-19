package dev.maicra.eruruupatch.compat.easyfarmers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Centralizes crop/tool decisions made by the Rich Farmer laboratory integration.
 *
 * This intentionally does not fabricate byproducts. Loot-driven crops receive the
 * real equipped Knife and Farmer's Delight/addons decide what that tool changes.
 * Special persistent crops can use the classification helpers below.
 */
public final class HarvestResolver {
    private static final ResourceLocation RED_MUSHROOM_COLONY = ResourceLocation.fromNamespaceAndPath(
            "farmersdelight", "red_mushroom_colony"
    );
    private static final ResourceLocation BROWN_MUSHROOM_COLONY = ResourceLocation.fromNamespaceAndPath(
            "farmersdelight", "brown_mushroom_colony"
    );
    private static final ResourceLocation BUDDING_TOMATOES = ResourceLocation.fromNamespaceAndPath(
            "farmersdelight", "budding_tomatoes"
    );
    private static final ResourceLocation TOMATOES = ResourceLocation.fromNamespaceAndPath(
            "farmersdelight", "tomatoes"
    );

    private HarvestResolver() {
    }

    public static ItemStack lootTool(boolean richVariant, ItemStack equippedKnife, ItemStack fallback) {
        if (richVariant && FarmerToolSupport.isKnife(equippedKnife)) {
            return equippedKnife.copy();
        }
        return fallback;
    }

    /** Mature Mushroom Colonies are the current persistent crop that requires a Knife gate. */
    public static boolean shouldWaitForKnife(boolean richVariant, BlockState crop, ItemStack equippedKnife) {
        return richVariant
                && isMushroomColony(crop)
                && isMature(crop)
                && !FarmerToolSupport.isKnife(equippedKnife);
    }

    public static boolean isMushroomColony(BlockState state) {
        ResourceLocation id = blockId(state);
        return RED_MUSHROOM_COLONY.equals(id) || BROWN_MUSHROOM_COLONY.equals(id);
    }

    /**
     * Tomatoes are intentionally classified separately because Farmer's Delight
     * harvests mature tomato vines through its no-item interaction, not a Knife
     * loot path. The sandbox therefore keeps the existing tomato behavior intact.
     */
    public static boolean isTomato(BlockState state) {
        ResourceLocation id = blockId(state);
        return BUDDING_TOMATOES.equals(id) || TOMATOES.equals(id);
    }

    public static boolean isMature(BlockState state) {
        if (state == null) {
            return false;
        }
        IntegerProperty age = ageProperty(state);
        if (age == null) {
            return false;
        }
        int current = state.getValue(age);
        int max = age.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(current);
        return current >= max;
    }

    public static IntegerProperty ageProperty(BlockState state) {
        if (state == null) {
            return null;
        }
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty && "age".equals(property.getName())) {
                return integerProperty;
            }
        }
        return null;
    }

    private static ResourceLocation blockId(BlockState state) {
        return state == null ? null : BuiltInRegistries.BLOCK.getKey(state.getBlock());
    }
}
