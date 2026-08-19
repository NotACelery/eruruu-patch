package dev.maicra.eruruupatch.recipe;

import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * Single Cutter recipe with a stable Oak display but data-preserving log variants.
 *
 * <p>The recipe book/JEI/EMI see one normal 3x3 shaped recipe whose result is the
 * canonical Oak Cutter while the log ingredient cycles/accepts #eruruu_patch:cutter_logs.
 * Runtime matching accepts that same tag. The log in the
 * bottom-center slot is copied into the result as Cutter BlockEntity data.</p>
 */
public final class CutterRecipe extends ShapedRecipe {
    public CutterRecipe(CraftingBookCategory category) {
        super(
                "eruruu_cutter",
                category,
                ShapedRecipePattern.of(
                        Map.of(
                                'G', Ingredient.of(Items.GLASS_PANE),
                                'C', Ingredient.of(cuttingBoard()),
                                'B', Ingredient.of(Items.BRICKS),
                                // One recipe entry; the ingredient remains the full allowed-log tag,
                                // while getResultItem() deliberately presents the canonical Oak Cutter.
                                'L', Ingredient.of(CutterLogVariant.ALLOWED_LOGS)
                        ),
                        "GGG",
                        "GCG",
                        "BLB"
                ),
                CutterLogVariant.createCutter(net.minecraft.world.level.block.Blocks.OAK_LOG),
                false
        );
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        if (input.size() < 8) {
            return ItemStack.EMPTY;
        }
        return CutterLogVariant.createCutter(CutterLogVariant.fromIngredient(input.getItem(7)));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // Always Oak/plain in recipe viewers. Other variants are produced at craft time.
        return CutterLogVariant.createCutter(net.minecraft.world.level.block.Blocks.OAK_LOG);
    }

    private static net.minecraft.world.item.Item cuttingBoard() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting_board"));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CUTTER.get();
    }
}
