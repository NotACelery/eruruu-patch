package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.integration.MobDropInfo;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import dev.maicra.eruruupatch.integration.SpecialCraftingInfo;
import dev.maicra.eruruupatch.integration.WorldInteractionInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

/** JEI documentation for Eruruu Patch world interactions, mob drops and special crafting. */
@JeiPlugin
public final class EruruuJeiPlugin implements IModPlugin {
    public static final RecipeType<WorldInteractionInfo> WORLD_INTERACTION =
            RecipeType.create(EruruuPatch.MOD_ID, "world_interaction", WorldInteractionInfo.class);
    public static final RecipeType<MobDropInfo> MOB_DROPS =
            RecipeType.create(EruruuPatch.MOD_ID, "mob_drops", MobDropInfo.class);
    public static final RecipeType<SpecialCraftingInfo> SPECIAL_CRAFTING =
            RecipeType.create(EruruuPatch.MOD_ID, "special_crafting", SpecialCraftingInfo.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WorldInteractionJeiCategory(gui),
                new MobDropJeiCategory(gui),
                new SpecialCraftingJeiCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WORLD_INTERACTION, RecipeViewerData.WORLD_INTERACTIONS);
        registration.addRecipes(MOB_DROPS, RecipeViewerData.MOB_DROPS);
        registration.addRecipes(SPECIAL_CRAFTING, RecipeViewerData.SPECIAL_CRAFTING);
    }
}
