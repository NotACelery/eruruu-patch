package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.menu.PaddyFarmerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Standard four-output Paddy screen, retaining the actual block name at top-left. */
public final class PaddyFarmerScreen extends AbstractContainerScreen<PaddyFarmerMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            "easy_villagers",
            "textures/gui/container/output.png"
    );

    public PaddyFarmerScreen(PaddyFarmerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 133;
        inventoryLabelY = 40;
        titleLabelY = 9;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
