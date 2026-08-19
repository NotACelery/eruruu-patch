package dev.maicra.eruruupatch.recipe;

import dev.maicra.eruruupatch.EruruuPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Recipe serializers still owned by Eruruu Patch after the 1.0.27 migration. */
public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, EruruuPatch.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EndlessCharcoalRecipe>> ENDLESS_CHARCOAL =
            RECIPE_SERIALIZERS.register("endless_charcoal",
                    () -> new SimpleCraftingRecipeSerializer<>(EndlessCharcoalRecipe::new));

    private ModRecipeSerializers() {
    }
}
