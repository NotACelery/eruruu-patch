package dev.maicra.eruruupatch.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.maicra.eruruupatch.integration.FarmerHarvestInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class FarmerHarvestEmiRecipe implements EmiRecipe {
    private final FarmerHarvestInfo info;
    private final EmiStack input;
    private final EmiIngredient tool;
    private final List<EmiStack> outputs;

    public FarmerHarvestEmiRecipe(FarmerHarvestInfo info) {
        this.info = info;
        this.input = EmiStack.of(info.input());
        this.tool = EmiIngredient.of(info.tool());
        this.outputs = info.outputs().stream().map(EmiStack::of).toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EruruuEmiPlugin.FARMER_HARVEST;
    }

    @Override
    public ResourceLocation getId() {
        return info.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(input);
        inputs.add(tool);
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 180;
    }

    @Override
    public int getDisplayHeight() {
        return 82;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(input, 8, 6);
        widgets.addSlot(tool, 48, 6);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 76, 6);

        int count = Math.min(4, outputs.size());
        for (int i = 0; i < count; i++) {
            widgets.addSlot(outputs.get(i), 108 + i * 18, 6).recipeContext(this);
        }

        int y = 36;
        widgets.addText(info.description(), 4, y, 0x555555, false);
    }
}
