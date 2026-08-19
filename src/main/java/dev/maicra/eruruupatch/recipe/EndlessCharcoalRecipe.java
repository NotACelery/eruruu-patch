package dev.maicra.eruruupatch.recipe;

import dev.maicra.eruruupatch.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Special crafting recipe that accepts exactly charcoal and requires at least
 * 64 total charcoal across the crafting grid. Consumption of the extra
 * charcoal is handled by CraftingEvents because vanilla crafting consumes
 * one item from each occupied slot.
 */
public final class EndlessCharcoalRecipe extends CustomRecipe {
    public static final int REQUIRED_CHARCOAL = 64;

    public EndlessCharcoalRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) {
            return false;
        }

        int total = 0;
        boolean foundAny = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!stack.is(Items.CHARCOAL)) {
                return false;
            }
            foundAny = true;
            total += stack.getCount();
        }

        return foundAny && total >= REQUIRED_CHARCOAL;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(ModItems.ENDLESS_CHARCOAL.get());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItems.ENDLESS_CHARCOAL.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ENDLESS_CHARCOAL.get();
    }
}
