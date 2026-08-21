package dev.maicra.eruruupatch.crafting;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * Stable identity for the meaningful contents of a crafting grid.
 *
 * Counts are deliberately ignored. Consuming one item from every ingredient
 * during repeated/shift crafting must not clear the selected recipe while the
 * actual ingredient pattern is unchanged. Item components remain part of the
 * signature so component-sensitive recipes still invalidate correctly.
 */
public final class CraftingInputSignature {
    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    private CraftingInputSignature() {
    }

    public static long of(CraftingContainer container) {
        long hash = begin(container.getWidth(), container.getHeight());
        int index = 0;
        for (ItemStack stack : container.getItems()) {
            hash = includeStack(hash, index++, stack);
        }
        return hash;
    }

    public static long of(CraftingInput input) {
        long hash = begin(input.width(), input.height());
        for (int index = 0; index < input.size(); index++) {
            hash = includeStack(hash, index, input.getItem(index));
        }
        return hash;
    }

    private static long begin(int width, int height) {
        long hash = FNV_OFFSET_BASIS;
        hash = include(hash, width);
        hash = include(hash, height);
        return hash;
    }

    private static long includeStack(long hash, int index, ItemStack stack) {
        hash = include(hash, index);
        if (stack.isEmpty()) {
            return include(hash, 0);
        }

        hash = include(hash, 1);
        return include(hash, ItemStack.hashItemAndComponents(stack));
    }

    private static long include(long hash, int value) {
        hash ^= value & 0xffffffffL;
        return hash * FNV_PRIME;
    }
}
