package dev.maicra.eruruupatch.integration.jei;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.integration.CutterAxeInfo;
import dev.maicra.eruruupatch.integration.FarmerHarvestInfo;
import dev.maicra.eruruupatch.integration.MobDropInfo;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import dev.maicra.eruruupatch.integration.SpecialCraftingInfo;
import dev.maicra.eruruupatch.integration.WorldInteractionInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class EruruuJeiPlugin implements IModPlugin {
    public static final RecipeType<WorldInteractionInfo> WORLD_INTERACTION =
            RecipeType.create(EruruuPatch.MOD_ID, "world_interaction", WorldInteractionInfo.class);
    public static final RecipeType<MobDropInfo> MOB_DROPS =
            RecipeType.create(EruruuPatch.MOD_ID, "mob_drops", MobDropInfo.class);
    public static final RecipeType<SpecialCraftingInfo> SPECIAL_CRAFTING =
            RecipeType.create(EruruuPatch.MOD_ID, "special_crafting", SpecialCraftingInfo.class);
    public static final RecipeType<FarmerHarvestInfo> FARMER_HARVEST =
            RecipeType.create(EruruuPatch.MOD_ID, "farmer_harvest", FarmerHarvestInfo.class);
    public static final RecipeType<CutterAxeInfo> CUTTER_AXE =
            RecipeType.create(EruruuPatch.MOD_ID, "cutter_axe", CutterAxeInfo.class);
    /** Legacy type kept only so old source trees compile; the category is intentionally not registered. */
    public static final RecipeType<dev.maicra.eruruupatch.integration.CutterVariantInfo> CUTTER_VARIANTS =
            RecipeType.create(EruruuPatch.MOD_ID, "cutter_variants", dev.maicra.eruruupatch.integration.CutterVariantInfo.class);

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
                new SpecialCraftingJeiCategory(gui),
                new FarmerHarvestJeiCategory(gui),
                new CutterAxeJeiCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WORLD_INTERACTION, RecipeViewerData.WORLD_INTERACTIONS);
        registration.addRecipes(MOB_DROPS, RecipeViewerData.MOB_DROPS);
        registration.addRecipes(SPECIAL_CRAFTING, RecipeViewerData.SPECIAL_CRAFTING);
        registration.addRecipes(FARMER_HARVEST, RecipeViewerData.FARMER_HARVESTS);
        registration.addRecipes(CUTTER_AXE, RecipeViewerData.cutterAxeActions());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Rich variants are the machines that actually perform Knife-aware harvesting.
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RICH_FARMER_ITEM.get()), FARMER_HARVEST);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RICH_PADDY_FARMER_ITEM.get()), FARMER_HARVEST);
        registration.addRecipeCatalyst(
                new ItemStack(dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get()),
                CUTTER_AXE
        );

        // Reuse Farmer's Delight's own Cutting category instead of copying every recipe.
        // Reflection keeps Farmer's Delight's implementation classes optional at compile time.
        registerFarmersDelightCuttingCatalyst(registration);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerFarmersDelightCuttingCatalyst(IRecipeCatalystRegistration registration) {
        // Farmer's Delight defines JEI CUTTING with RecipeType.createFromVanilla
        // around the registered farmersdelight:cutting vanilla recipe type. Build
        // the exact same JEI key ourselves instead of reflectively loading FD's
        // optional JEI implementation class; this is stable across classloaders.
        net.minecraft.world.item.crafting.RecipeType<?> vanillaCutting = BuiltInRegistries.RECIPE_TYPE.get(
                ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting")
        );
        if (vanillaCutting == null) {
            return;
        }
        RecipeType cuttingType = RecipeType.createFromVanilla((net.minecraft.world.item.crafting.RecipeType) vanillaCutting);
        registration.addRecipeCatalyst(
                new ItemStack(dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get()),
                cuttingType
        );
    }

}
