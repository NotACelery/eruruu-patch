package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.integration.CutterVariantInfo;
import dev.maicra.eruruupatch.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** JEI documentation category for the material-dependent Cutter BlockItem variants. */
public final class CutterVariantsJeiCategory implements IRecipeCategory<CutterVariantInfo> {
    private final IDrawable icon;

    public CutterVariantsJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CUTTER.get()));
    }

    @Override
    public RecipeType<CutterVariantInfo> getRecipeType() {
        return EruruuJeiPlugin.CUTTER_VARIANTS;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.cutter_variants");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 42;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CutterVariantInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(14, 8).addItemStack(recipe.material());
        builder.addOutputSlot(84, 8).addItemStack(recipe.cutter());
    }

    @Override
    public void draw(CutterVariantInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "→", 52, 13, 0x404040, false);
        int labelX = Math.max(4, (getWidth() - font.width(recipe.variantName())) / 2);
        graphics.drawString(font, recipe.variantName(), labelX, 31, 0x555555, false);
    }
}
