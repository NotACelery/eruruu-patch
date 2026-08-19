package dev.maicra.eruruupatch.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.maicra.eruruupatch.integration.CutterAxeInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class CutterAxeEmiRecipe implements EmiRecipe {
    private final CutterAxeInfo info;
    private final EmiStack input;
    private final EmiIngredient tool;
    private final EmiStack output;

    public CutterAxeEmiRecipe(CutterAxeInfo info) {
        this.info = info;
        this.input = EmiStack.of(info.input());
        this.tool = EmiIngredient.of(info.tool());
        this.output = EmiStack.of(info.output());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EruruuEmiPlugin.CUTTER_AXE;
    }

    @Override
    public ResourceLocation getId() {
        return info.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input, tool);
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
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(input, 12, 6);
        widgets.addSlot(tool, 50, 6);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 78, 6);
        widgets.addSlot(output, 116, 6).recipeContext(this);
        widgets.addText(info.description(), 8, 36, 0x555555, false);
    }
}
