package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.integration.MobDropInfo;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import dev.maicra.eruruupatch.integration.WorldInteractionInfo;
import dev.maicra.eruruupatch.registry.ModBlocks;
import java.util.Set;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class EruruuJeiPlugin implements IModPlugin {
    private static final Set<String> CHARCOAL_RECIPE_PATHS = Set.of(
            "charcoal_block",
            "charcoal_from_charcoal_block",
            "endless_charcoal"
    );

    public static final RecipeType<WorldInteractionInfo> WORLD_INTERACTION =
            RecipeType.create(EruruuPatch.MOD_ID, "world_interaction", WorldInteractionInfo.class);
    public static final RecipeType<MobDropInfo> MOB_DROPS =
            RecipeType.create(EruruuPatch.MOD_ID, "mob_drops", MobDropInfo.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WorldInteractionJeiCategory(gui),
                new MobDropJeiCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WORLD_INTERACTION, RecipeViewerData.WORLD_INTERACTIONS);
        registration.addRecipes(MOB_DROPS, RecipeViewerData.MOB_DROPS);
        registration.addIngredientInfo(
                ModBlocks.FILTERED_HOPPER.get(),
                Component.translatable("eruruu_patch.viewer.filtered_hopper")
        );

        var level = Minecraft.getInstance().level;
        if (level != null) {
            var charcoalRecipes = level.getRecipeManager()
                    .getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)
                    .stream()
                    .filter(holder -> holder.id().getNamespace().equals(EruruuPatch.MOD_ID))
                    .filter(holder -> CHARCOAL_RECIPE_PATHS.contains(holder.id().getPath()))
                    .toList();

            if (!charcoalRecipes.isEmpty()) {
                registration.addRecipes(RecipeTypes.CRAFTING, charcoalRecipes);
            }
        }
    }
}
