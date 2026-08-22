package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.menu.FilteredHopperMenu;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class FilteredHopperScreen extends AbstractContainerScreen<FilteredHopperMenu> {
    private static final ResourceLocation HOPPER_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/hopper.png");

    public FilteredHopperScreen(FilteredHopperMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (menu.getCarried().isEmpty()
                && hoveredSlot != null
                && hoveredSlot.index == FilteredHopperMenu.FILTER_SLOT
                && !hoveredSlot.hasItem()) {
            graphics.renderComponentTooltip(
                    font,
                    List.of(
                            Component.translatable("gui.eruruu_patch.filtered_hopper.filter_slot")
                                    .withStyle(ChatFormatting.GOLD),
                            Component.translatable("gui.eruruu_patch.filtered_hopper.filter_hint")
                                    .withStyle(ChatFormatting.GRAY)
                    ),
                    mouseX,
                    mouseY
            );
            return;
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(HOPPER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        // Reuse the vanilla first-slot frame so the filter slot is visually native.
        graphics.blit(HOPPER_TEXTURE, x + 151, y + 19, 43, 19, 18, 18);
    }
}
