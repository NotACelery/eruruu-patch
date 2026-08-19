package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.integration.SpecialCraftingInfo;
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

public final class SpecialCraftingJeiCategory implements IRecipeCategory<SpecialCraftingInfo> {
    private final IDrawable icon;

    public SpecialCraftingJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.ENDLESS_CHARCOAL.get()));
    }

    @Override
    public RecipeType<SpecialCraftingInfo> getRecipeType() {
        return EruruuJeiPlugin.SPECIAL_CRAFTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.special_crafting");
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
        return 62;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpecialCraftingInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(24, 8).addItemStack(recipe.input());
        builder.addOutputSlot(108, 8).addItemStack(recipe.output());
    }

    @Override
    public void draw(SpecialCraftingInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "→", 70, 13, 0x404040, false);
        int y = 34;
        for (var line : font.split(recipe.description(), 142)) {
            graphics.drawString(font, line, 4, y, 0x555555, false);
            y += 9;
            if (y > 58) break;
        }
    }
}
