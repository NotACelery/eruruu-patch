package dev.maicra.eruruupatch.integration.jei;

import dev.maicra.eruruupatch.integration.MobDropInfo;
import dev.maicra.eruruupatch.integration.RecipeViewerData;
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
import net.minecraft.world.item.Items;

public final class MobDropJeiCategory implements IRecipeCategory<MobDropInfo> {
    private final IDrawable icon;

    public MobDropJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.GOLD_INGOT));
    }

    @Override
    public RecipeType<MobDropInfo> getRecipeType() {
        return EruruuJeiPlugin.MOB_DROPS;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.eruruu_patch.mob_drops");
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
        return 86;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MobDropInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(16, 8).addItemStack(recipe.mobIcon());
        builder.addOutputSlot(116, 8).addItemStack(recipe.output());
    }

    @Override
    public void draw(MobDropInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "→", 71, 13, 0x404040, false);

        graphics.drawString(font, Component.translatable("eruruu_patch.viewer.base", RecipeViewerData.percent(recipe.chanceAtLooting(0))), 8, 33, 0x404040, false);
        graphics.drawString(font, Component.translatable("eruruu_patch.viewer.looting", "I", RecipeViewerData.percent(recipe.chanceAtLooting(1))), 8, 43, 0x555555, false);
        graphics.drawString(font, Component.translatable("eruruu_patch.viewer.looting", "II", RecipeViewerData.percent(recipe.chanceAtLooting(2))), 8, 53, 0x555555, false);
        graphics.drawString(font, Component.translatable("eruruu_patch.viewer.looting", "III", RecipeViewerData.percent(recipe.chanceAtLooting(3))), 8, 63, 0x555555, false);
        if (recipe.requiresPlayerKill()) {
            graphics.drawString(font, Component.translatable("eruruu_patch.viewer.killed_by_player"), 82, 34, 0x777777, false);
        }
        if (recipe.easyMobFarmCompatible()) {
            graphics.drawString(font, Component.translatable("eruruu_patch.viewer.easy_mob_farm"), 82, 46, 0x777777, false);
        }
    }
}
