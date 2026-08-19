package dev.maicra.eruruupatch.recipe;

import dev.maicra.eruruupatch.EruruuPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, EruruuPatch.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EndlessCharcoalRecipe>> ENDLESS_CHARCOAL =
            RECIPE_SERIALIZERS.register("endless_charcoal",
                    () -> new SimpleCraftingRecipeSerializer<>(EndlessCharcoalRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CutterRecipe>> CUTTER =
            RECIPE_SERIALIZERS.register("cutter",
                    () -> new SimpleCraftingRecipeSerializer<>(CutterRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FarmerUpgradeDisplayRecipe>> PADDY_FARMER_DISPLAY =
            RECIPE_SERIALIZERS.register("paddy_farmer_display",
                    () -> new SimpleCraftingRecipeSerializer<>(category ->
                            new FarmerUpgradeDisplayRecipe(category, FarmerUpgradeDisplayRecipe.Variant.PADDY)));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FarmerUpgradeDisplayRecipe>> RICH_FARMER_DISPLAY =
            RECIPE_SERIALIZERS.register("rich_farmer_display",
                    () -> new SimpleCraftingRecipeSerializer<>(category ->
                            new FarmerUpgradeDisplayRecipe(category, FarmerUpgradeDisplayRecipe.Variant.RICH)));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FarmerUpgradeDisplayRecipe>> RICH_PADDY_FARMER_DISPLAY =
            RECIPE_SERIALIZERS.register("rich_paddy_farmer_display",
                    () -> new SimpleCraftingRecipeSerializer<>(category ->
                            new FarmerUpgradeDisplayRecipe(category, FarmerUpgradeDisplayRecipe.Variant.RICH_PADDY)));

    private ModRecipeSerializers() {
    }
}
