package dev.maicra.eruruupatch.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.maicra.eruruupatch.integration.MobDropInfo;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class MobDropEmiRecipe implements EmiRecipe {
    private final MobDropInfo info;
    private final EmiStack mob;
    private final EmiStack output;

    public MobDropEmiRecipe(MobDropInfo info) {
        this.info = info;
        this.mob = EmiStack.of(info.mobIcon());
        this.output = EmiStack.of(info.output()).setChance(info.baseChance());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EruruuEmiPlugin.MOB_DROPS;
    }

    @Override
    public ResourceLocation getId() {
        return info.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(mob);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 82;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(mob, 8, 5);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 54, 5);
        widgets.addSlot(output, 116, 5).recipeContext(this);

        widgets.addText(
                Component.translatable(
                        "eruruu_patch.viewer.base",
                        RecipeViewerData.percent(info.chanceAtLooting(0))),
                8,
                29,
                0x404040,
                false);
        widgets.addText(
                Component.translatable(
                        "eruruu_patch.viewer.looting",
                        "I",
                        RecipeViewerData.percent(info.chanceAtLooting(1))),
                8,
                39,
                0x555555,
                false);
        widgets.addText(
                Component.translatable(
                        "eruruu_patch.viewer.looting",
                        "II",
                        RecipeViewerData.percent(info.chanceAtLooting(2))),
                8,
                49,
                0x555555,
                false);
        widgets.addText(
                Component.translatable(
                        "eruruu_patch.viewer.looting",
                        "III",
                        RecipeViewerData.percent(info.chanceAtLooting(3))),
                8,
                59,
                0x555555,
                false);
        if (info.requiresPlayerKill()) {
            widgets.addText(Component.translatable("eruruu_patch.viewer.killed_by_player"), 80, 30, 0x777777, false);
        }
        if (info.easyMobFarmCompatible()) {
            widgets.addText(Component.translatable("eruruu_patch.viewer.easy_mob_farm"), 80, 42, 0x777777, false);
        }
    }
}
