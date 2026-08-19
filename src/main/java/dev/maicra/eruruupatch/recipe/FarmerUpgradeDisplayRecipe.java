package dev.maicra.eruruupatch.recipe;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

/**
 * Recipe-book/viewer representation of Easy Farmer's Delight Compat's data-preserving
 * farmer upgrade recipes.
 *
 * <p>The real recipes continue to be owned by Easy Farmer's Delight Compat because
 * they must copy the center Farmer BlockItem's BlockEntity data into the upgraded
 * result. These recipes deliberately NEVER match crafting input. Their only job is
 * to expose a real 3x3 shaped layout and result to the vanilla recipe book, JEI and
 * EMI. After the recipe book places the ingredients, the original EasyFD recipe is
 * the recipe that actually matches and assembles the output.</p>
 *
 * <p>This extends vanilla {@link ShapedRecipe} directly. Minecraft 1.21.1/NeoForge
 * does not expose the old Forge {@code IShapedRecipe} interface, and the vanilla
 * recipe-book placer itself checks for {@code ShapedRecipe} when it needs recipe
 * dimensions. Using the vanilla type therefore keeps placement/viewer semantics
 * correct without relying on a removed compatibility interface.</p>
 */
public final class FarmerUpgradeDisplayRecipe extends ShapedRecipe {
    public enum Variant {
        PADDY,
        RICH,
        RICH_PADDY
    }

    private static final ResourceLocation EASY_VILLAGERS_FARMER =
            ResourceLocation.fromNamespaceAndPath("easy_villagers", "farmer");
    private static final ResourceLocation RICH_SOIL =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil");

    private final Variant variant;

    public FarmerUpgradeDisplayRecipe(CraftingBookCategory category, Variant variant) {
        super(
                "eruruu_farmer_upgrade_display",
                category,
                createPattern(variant),
                resultFor(variant),
                false
        );
        this.variant = variant;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        // Display-only. The authoritative EasyFD custom recipe performs the craft.
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Never manufacture a data-less farmer. EasyFD owns the real assembly step.
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return switch (variant) {
            case PADDY -> ModRecipeSerializers.PADDY_FARMER_DISPLAY.get();
            case RICH -> ModRecipeSerializers.RICH_FARMER_DISPLAY.get();
            case RICH_PADDY -> ModRecipeSerializers.RICH_PADDY_FARMER_DISPLAY.get();
        };
    }

    private static ShapedRecipePattern createPattern(Variant variant) {
        Ingredient glass = Ingredient.of(Items.GLASS_PANE);

        if (variant == Variant.PADDY) {
            return ShapedRecipePattern.of(
                    Map.of(
                            'G', glass,
                            'F', Ingredient.of(item(EASY_VILLAGERS_FARMER)),
                            'I', Ingredient.of(Items.IRON_INGOT),
                            'W', Ingredient.of(Items.WATER_BUCKET)
                    ),
                    "GGG",
                    "GFG",
                    "IWI"
            );
        }

        Ingredient center = variant == Variant.RICH
                ? Ingredient.of(item(EASY_VILLAGERS_FARMER))
                : Ingredient.of(ModBlocks.PADDY_FARMER_ITEM.get());

        return ShapedRecipePattern.of(
                Map.of(
                        'G', glass,
                        'F', center,
                        'I', Ingredient.of(Items.IRON_BLOCK),
                        'R', Ingredient.of(item(RICH_SOIL))
                ),
                "GGG",
                "GFG",
                "IRI"
        );
    }

    private static ItemStack resultFor(Variant variant) {
        return switch (variant) {
            case PADDY -> new ItemStack(ModBlocks.PADDY_FARMER_ITEM.get());
            case RICH -> new ItemStack(ModBlocks.RICH_FARMER_ITEM.get());
            case RICH_PADDY -> new ItemStack(ModBlocks.RICH_PADDY_FARMER_ITEM.get());
        };
    }

    private static Item item(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }
}
