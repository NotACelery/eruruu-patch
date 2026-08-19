package dev.maicra.eruruupatch.integration.jei;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.maicra.eruruupatch.integration.FarmerHarvestInfo;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class FarmerHarvestJeiCategory implements IRecipeCategory<FarmerHarvestInfo> {
    private final IDrawable icon;

    public FarmerHarvestJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.RICH_FARMER_ITEM.get()));
    }

    @Override
    public RecipeType<FarmerHarvestInfo> getRecipeType() {
        return EruruuJeiPlugin.FARMER_HARVEST;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.farmer_harvest");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 82;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FarmerHarvestInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(8, 8).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 48, 8).addIngredients(recipe.tool());

        int count = Math.min(4, recipe.outputs().size());
        int startX = 104;
        for (int i = 0; i < count; i++) {
            builder.addOutputSlot(startX + i * 18, 8).addItemStack(recipe.outputs().get(i));
        }
    }

    @Override
    public void draw(FarmerHarvestInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 35, 13, 0x404040, false);
        graphics.drawString(font, "→", 82, 13, 0x404040, false);

        int y = 36;
        for (var line : font.split(recipe.description(), 150)) {
            graphics.drawString(font, line, 4, y, 0x555555, false);
            y += 9;
            if (y > 78) {
                break;
            }
        }
    }
}
