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
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

/** EMI documentation for Eruruu Patch world interactions, mob drops and special crafting. */
@EmiEntrypoint
public final class EruruuEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory MOB_DROPS = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "mob_drops"),
            EmiStack.of(ModItems.ERURUU_ICON.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MOB_DROPS);

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

        for (var ignored : RecipeViewerData.SPECIAL_CRAFTING) {
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
    }
}
