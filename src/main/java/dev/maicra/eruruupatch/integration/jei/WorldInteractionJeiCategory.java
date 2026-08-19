package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
import dev.maicra.eruruupatch.integration.WorldInteractionInfo;
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

public final class WorldInteractionJeiCategory implements IRecipeCategory<WorldInteractionInfo> {
    private final IDrawable icon;

    public WorldInteractionJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.FERTILIZER.get()));
    }

    @Override
    public RecipeType<WorldInteractionInfo> getRecipeType() {
        return EruruuJeiPlugin.WORLD_INTERACTION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.world_interaction");
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
        return 98;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WorldInteractionInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(8, 8).addItemStack(recipe.leftInput());
        builder.addSlot(RecipeIngredientRole.CATALYST, 48, 8).addItemStack(recipe.rightInput());
        builder.addOutputSlot(116, 8).addItemStack(recipe.output());
    }

    @Override
    public void draw(WorldInteractionInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 35, 13, 0x404040, false);
        graphics.drawString(font, "→", 88, 13, 0x404040, false);
        if (recipe.chance() < 0.9999F) {
            Component chance = Component.translatable("eruruu_patch.viewer.chance", RecipeViewerData.percent(recipe.chance()));
            graphics.drawString(font, chance, 8, 34, 0x404040, false);
        }
        int y = 47;
        for (var line : font.split(recipe.description(), 142)) {
            graphics.drawString(font, line, 8, y, 0x555555, false);
            y += 9;
            if (y > 94) break;
        }
    }
}
