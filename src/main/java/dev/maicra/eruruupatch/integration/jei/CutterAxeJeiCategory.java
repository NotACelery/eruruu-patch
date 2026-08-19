package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.integration.CutterAxeInfo;
import dev.maicra.eruruupatch.registry.ModBlocks;
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

public final class CutterAxeJeiCategory implements IRecipeCategory<CutterAxeInfo> {
    private final IDrawable icon;

    public CutterAxeJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CUTTER.get()));
    }

    @Override
    public RecipeType<CutterAxeInfo> getRecipeType() {
        return EruruuJeiPlugin.CUTTER_AXE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.cutter_axe");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 58;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CutterAxeInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(12, 8).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 50, 8).addIngredients(recipe.tool());
        builder.addOutputSlot(116, 8).addItemStack(recipe.output());
    }

    @Override
    public void draw(CutterAxeInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 38, 13, 0x404040, false);
        graphics.drawString(font, "→", 88, 13, 0x404040, false);
        graphics.drawString(font, recipe.description(), 8, 36, 0x555555, false);
    }
}
