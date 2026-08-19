package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import dev.maicra.eruruupatch.menu.CutterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Easy-Villagers Breeder-style Cutter GUI with a tool slot beside the inputs. */
public final class CutterScreen extends AbstractContainerScreen<CutterMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            "easy_villagers", "textures/gui/container/input_output.png"
    );
    private static final ResourceLocation EASY_OUTPUT = ResourceLocation.fromNamespaceAndPath(
            "easy_villagers", "textures/gui/container/output.png"
    );
    private static final ResourceLocation EMPTY_TOOL = ResourceLocation.fromNamespaceAndPath(
            "eruruu_patch", "textures/item/empty_knife_slot.png"
    );

    public CutterScreen(CutterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 164;
        inventoryLabelX = 8;
        inventoryLabelY = 71;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        // Use Easy Villagers' own Breeder/InputOutput background and add only the
        // extra processing-tool slot at the same X/Y used by our Rich Farmer slot.
        graphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
        graphics.blit(EASY_OUTPUT, x + 141, y + 19, 51, 19, 18, 18);

        if (!menu.getSlot(CutterMenu.TOOL_SLOT).hasItem()) {
            graphics.blit(EMPTY_TOOL, x + 142, y + 20, 0, 0, 16, 16, 16, 16);
        }

        int progressWidth = Math.round(16.0F * menu.progress() / CutterBlockEntity.PROCESS_TICKS);
        graphics.fill(x + 142, y + 42, x + 142 + progressWidth, y + 44, 0xFF6B8E23);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Keep Easy Villagers' InputOutput coordinates, but also retain the block
        // title at the upper-left like the Rich Farmer screens. drawCenteredString
        // adds a shadow and looked doubled/darker at GUI scale, so all labels use
        // the same no-shadow drawString path as Easy Villagers' ScreenBase.
        graphics.drawString(font, title, 8, 9, 0x404040, false);
        drawCenteredNoShadow(graphics, Component.translatable("gui.eruruu_patch.cutter.input"), 88, 9);
        drawCenteredNoShadow(graphics, Component.translatable("gui.eruruu_patch.cutter.output"), 88, 40);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    private void drawCenteredNoShadow(GuiGraphics graphics, Component text, int centerX, int y) {
        graphics.drawString(font, text, centerX - font.width(text) / 2, y, 0x404040, false);
    }
}
