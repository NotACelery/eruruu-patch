package dev.maicra.eruruupatch.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import dev.maicra.eruruupatch.registry.ModBlocks;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

@EmiEntrypoint
public final class EruruuEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory MOB_DROPS = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "mob_drops"),
            EmiStack.of(ModItems.ERURUU_ICON.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MOB_DROPS);

        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(ModBlocks.FILTERED_HOPPER_ITEM.get())),
                List.of(Component.translatable("eruruu_patch.viewer.filtered_hopper")),
                ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "info/filtered_hopper")
        ));

        for (var info : RecipeViewerData.WORLD_INTERACTIONS) {
            var output = EmiStack.of(info.output());
            if (info.chance() < 0.9999F) output.setChance(info.chance());
            registry.addRecipe(EmiWorldInteractionRecipe.builder()
                    .id(info.id())
                    .leftInput(EmiStack.of(info.leftInput()), slot -> slot.appendTooltip(info.description()))
                    .rightInput(EmiStack.of(info.rightInput()), true, slot -> slot.appendTooltip(info.description()))
                    .output(output, slot -> info.chance() < 0.9999F
                            ? slot.appendTooltip(net.minecraft.network.chat.Component.translatable(
                                    "eruruu_patch.viewer.chance", RecipeViewerData.percent(info.chance())))
                            : slot)
                    .build());
        }

        registerCharcoalRecipeFallbacks(registry);

        for (var info : RecipeViewerData.MOB_DROPS) {
            registry.addRecipe(new MobDropEmiRecipe(info));
        }
    }

    private static void registerCharcoalRecipeFallbacks(EmiRegistry registry) {

        registry.removeRecipes(id("charcoal_block"));
        registry.removeRecipes(id("charcoal_from_charcoal_block"));
        registry.removeRecipes(id("endless_charcoal"));

        registry.addRecipe(new EmiCraftingRecipe(
                nine(EmiStack.of(Items.CHARCOAL)),
                EmiStack.of(ModBlocks.CHARCOAL_BLOCK_ITEM.get()),
                syntheticId("charcoal_block"),
                false
        ));

        registry.addRecipe(new EmiCraftingRecipe(
                List.of(EmiStack.of(ModBlocks.CHARCOAL_BLOCK_ITEM.get())),
                EmiStack.of(Items.CHARCOAL, 9),
                syntheticId("charcoal_from_charcoal_block"),
                true
        ));

        registry.addRecipe(new EmiCraftingRecipe(
                nine(EmiStack.of(ModBlocks.CHARCOAL_BLOCK_ITEM.get())),
                EmiStack.of(ModItems.ENDLESS_CHARCOAL.get()),
                syntheticId("endless_charcoal"),
                false
        ));
    }

    private static List<EmiIngredient> nine(EmiStack stack) {
        return List.of(
                stack.copy(), stack.copy(), stack.copy(),
                stack.copy(), stack.copy(), stack.copy(),
                stack.copy(), stack.copy(), stack.copy()
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, path);
    }

    private static ResourceLocation syntheticId(String path) {
        return ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "/crafting/" + path);
    }
}
