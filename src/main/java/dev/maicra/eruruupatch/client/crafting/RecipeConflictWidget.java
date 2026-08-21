package dev.maicra.eruruupatch.client.crafting;

import dev.maicra.eruruupatch.crafting.CraftingRecipeCandidate;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

final class RecipeConflictWidget {
    static final int MAX_VISIBLE_ROWS = 8;
    private static final int BUTTON_SIZE = 12;
    private static final int ROW_HEIGHT = 20;
    private static final int POPUP_PADDING = 2;
    private static final int POPUP_WIDTH = 144;

    private RecipeConflictWidget() {
    }

    static Layout layout(
            AbstractContainerScreen<?> screen,
            CraftingConflictClientState.Snapshot snapshot
    ) {
        Slot resultSlot = snapshot.menu().getSlot(snapshot.recipeBookMenu().getResultSlotIndex());
        int resultX = screen.getGuiLeft() + resultSlot.x;
        int resultY = screen.getGuiTop() + resultSlot.y;

        int guiRight = screen.getGuiLeft() + screen.getXSize();
        int buttonX = resultX + 19;
        if (buttonX + BUTTON_SIZE > guiRight - 3) {
            buttonX = resultX - BUTTON_SIZE - 3;
        }
        int buttonY = resultY + 2;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int popupWidth = Math.min(POPUP_WIDTH, Math.max(96, screen.getXSize() - 8));

        int popupX = buttonX - popupWidth - 3;
        int minimumX = Math.max(4, screen.getGuiLeft() + 4);
        int maximumX = Math.min(screenWidth - popupWidth - 4, guiRight - popupWidth - 4);
        popupX = clamp(popupX, minimumX, Math.max(minimumX, maximumX));

        int desiredRows = Math.min(MAX_VISIBLE_ROWS, snapshot.choices().size());
        int availableBelow = Math.max(1, (screenHeight - (buttonY + BUTTON_SIZE + 6)) / ROW_HEIGHT);
        int visibleRows = Math.max(1, Math.min(desiredRows, availableBelow));
        int popupHeight = visibleRows * ROW_HEIGHT + POPUP_PADDING * 2;
        int popupY = buttonY + BUTTON_SIZE + 2;
        if (popupY + popupHeight > screenHeight - 4) {
            popupY = Math.max(4, buttonY - popupHeight - 2);
        }

        int maxScroll = Math.max(0, snapshot.choices().size() - visibleRows);
        int firstRow = Math.min(snapshot.scrollOffset(), maxScroll);

        return new Layout(
                buttonX,
                buttonY,
                popupX,
                popupY,
                popupWidth,
                popupHeight,
                visibleRows,
                firstRow
        );
    }

    static void render(
            GuiGraphics graphics,
            AbstractContainerScreen<?> screen,
            CraftingConflictClientState.Snapshot snapshot,
            int mouseX,
            int mouseY
    ) {
        if (snapshot.choices().size() < 2) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Layout layout = layout(screen, snapshot);
        boolean buttonHovered = layout.insideButton(mouseX, mouseY);

        graphics.fill(
                layout.buttonX(),
                layout.buttonY(),
                layout.buttonX() + BUTTON_SIZE,
                layout.buttonY() + BUTTON_SIZE,
                buttonHovered ? 0xff707070 : 0xff3f3f3f
        );
        graphics.renderOutline(
                layout.buttonX(),
                layout.buttonY(),
                BUTTON_SIZE,
                BUTTON_SIZE,
                buttonHovered ? 0xffffffff : 0xffa0a0a0
        );
        Component symbol = Component.literal("↻");
        int symbolX = layout.buttonX() + (BUTTON_SIZE - minecraft.font.width(symbol)) / 2;
        graphics.drawString(minecraft.font, symbol, symbolX, layout.buttonY() + 2, 0xffffffff, false);

        if (buttonHovered) {
            graphics.renderTooltip(
                    minecraft.font,
                    Component.translatable("gui.eruruu_patch.switch_recipe_result"),
                    mouseX,
                    mouseY
            );
        }

        if (!snapshot.popupOpen()) {
            return;
        }

        graphics.fill(
                layout.popupX(),
                layout.popupY(),
                layout.popupX() + layout.popupWidth(),
                layout.popupY() + layout.popupHeight(),
                0xee202020
        );
        graphics.renderOutline(
                layout.popupX(),
                layout.popupY(),
                layout.popupWidth(),
                layout.popupHeight(),
                0xffa0a0a0
        );

        List<CraftingRecipeCandidate> choices = snapshot.choices();
        for (int row = 0; row < layout.visibleRows(); row++) {
            int candidateIndex = layout.firstRow() + row;
            if (candidateIndex >= choices.size()) {
                break;
            }

            CraftingRecipeCandidate candidate = choices.get(candidateIndex);
            int rowX = layout.popupX() + POPUP_PADDING;
            int rowY = layout.popupY() + POPUP_PADDING + row * ROW_HEIGHT;
            int rowWidth = layout.popupWidth() - POPUP_PADDING * 2;
            boolean hovered = inside(mouseX, mouseY, rowX, rowY, rowWidth, ROW_HEIGHT);
            boolean selected = candidate.id().equals(snapshot.selectedRecipeId());

            if (selected) {
                graphics.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT, 0x884a6b8a);
            } else if (hovered) {
                graphics.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT, 0x665f5f5f);
            }

            graphics.renderItem(candidate.output(), rowX + 2, rowY + 2);
            String name = candidate.output().getHoverName().getString();
            String label = "[" + candidate.id().getNamespace() + "] " + name;
            int maxNameWidth = rowWidth - 28;
            String clipped = minecraft.font.plainSubstrByWidth(label, maxNameWidth);
            graphics.drawString(minecraft.font, clipped, rowX + 22, rowY + 6, 0xffffffff, false);

            if (selected) {
                graphics.drawString(minecraft.font, ">", rowX + rowWidth - 7, rowY + 6, 0xffffffff, false);
            }

            if (hovered) {
                graphics.renderTooltip(minecraft.font, candidate.output(), mouseX, mouseY);
            }
        }
    }

    static int clickedChoice(
            Layout layout,
            CraftingConflictClientState.Snapshot snapshot,
            double mouseX,
            double mouseY
    ) {
        if (!snapshot.popupOpen() || !layout.insidePopup(mouseX, mouseY)) {
            return -1;
        }

        int relativeY = (int) mouseY - layout.popupY() - POPUP_PADDING;
        if (relativeY < 0) {
            return -1;
        }
        int row = relativeY / ROW_HEIGHT;
        if (row < 0 || row >= layout.visibleRows()) {
            return -1;
        }

        int candidateIndex = layout.firstRow() + row;
        return candidateIndex < snapshot.choices().size() ? candidateIndex : -1;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean inside(double x, double y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    record Layout(
            int buttonX,
            int buttonY,
            int popupX,
            int popupY,
            int popupWidth,
            int popupHeight,
            int visibleRows,
            int firstRow
    ) {
        boolean insideButton(double mouseX, double mouseY) {
            return inside(mouseX, mouseY, buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);
        }

        boolean insidePopup(double mouseX, double mouseY) {
            return inside(mouseX, mouseY, popupX, popupY, popupWidth, popupHeight);
        }
    }
}
