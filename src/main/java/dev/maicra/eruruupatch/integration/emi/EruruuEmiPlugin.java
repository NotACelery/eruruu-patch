package dev.maicra.eruruupatch.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.List;

@EmiEntrypoint
public final class EruruuEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory MOB_DROPS = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "mob_drops"),
            EmiStack.of(ModItems.ERURUU_ICON.get())
    );

    public static final EmiRecipeCategory FARMER_HARVEST = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "farmer_harvest"),
            EmiStack.of(dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks.RICH_FARMER_ITEM.get())
    );

    public static final EmiRecipeCategory CUTTER_AXE = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "cutter_axe"),
            EmiStack.of(dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MOB_DROPS);
        registry.addCategory(FARMER_HARVEST);
        registry.addCategory(CUTTER_AXE);

        registry.addWorkstation(
                FARMER_HARVEST,
                EmiStack.of(dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks.RICH_FARMER_ITEM.get())
        );
        registry.addWorkstation(
                FARMER_HARVEST,
                EmiStack.of(dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks.RICH_PADDY_FARMER_ITEM.get())
        );
        registry.addWorkstation(
                CUTTER_AXE,
                EmiStack.of(dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get())
        );

        // Make the automated Cutter a workstation for Farmer's Delight's native Cutting category.
        registerFarmersDelightCuttingWorkstation(registry);

        for (var info : RecipeViewerData.WORLD_INTERACTIONS) {
            var output = EmiStack.of(info.output());
            if (info.chance() < 0.9999F) {
                output.setChance(info.chance());
            }
            registry.addRecipe(EmiWorldInteractionRecipe.builder()
                    .id(info.id())
                    .leftInput(EmiStack.of(info.leftInput()), slot -> slot.appendTooltip(info.description()))
                    .rightInput(EmiStack.of(info.rightInput()), true, slot -> slot.appendTooltip(info.description()))
                    .output(output, slot -> info.chance() < 0.9999F
                            ? slot.appendTooltip(net.minecraft.network.chat.Component.translatable(
                                    "eruruu_patch.viewer.chance",
                                    RecipeViewerData.percent(info.chance())))
                            : slot)
                    .build());
        }

        for (var info : RecipeViewerData.SPECIAL_CRAFTING) {
            registry.addRecipe(new EmiCraftingRecipe(
                    List.of(EmiStack.of(Items.CHARCOAL, 64)),
                    EmiStack.of(ModItems.ENDLESS_CHARCOAL.get()),
                    ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "/crafting/endless_charcoal"),
                    true
            ));
        }

        for (var info : RecipeViewerData.MOB_DROPS) {
            registry.addRecipe(new MobDropEmiRecipe(info));
        }

        for (var info : RecipeViewerData.FARMER_HARVESTS) {
            registry.addRecipe(new FarmerHarvestEmiRecipe(info));
        }

        for (var info : RecipeViewerData.cutterAxeActions()) {
            registry.addRecipe(new CutterAxeEmiRecipe(info));
        }
    }

    private static void registerFarmersDelightCuttingWorkstation(EmiRegistry registry) {
        // FD owns the native EMI category object, so use that exact instance.
        // NeoForge's mod layer can make Class.forName from another mod's loader
        // unreliable; try the active context loader first, then the local loader.
        Class<?> categories = loadOptionalClass("vectorwing.farmersdelight.integration.emi.FDRecipeCategories");
        if (categories == null) {
            return;
        }
        try {
            var field = categories.getDeclaredField("CUTTING");
            field.trySetAccessible();
            Object value = field.get(null);
            if (value instanceof EmiRecipeCategory cutting) {
                registry.addWorkstation(
                        cutting,
                        EmiStack.of(dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get())
                );
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // FD's EMI implementation is optional. Our own Cutter categories remain available.
        }
    }

    private static Class<?> loadOptionalClass(String name) {
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        if (context != null) {
            try {
                return Class.forName(name, true, context);
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError ignored) {
            return null;
        }
    }
}
