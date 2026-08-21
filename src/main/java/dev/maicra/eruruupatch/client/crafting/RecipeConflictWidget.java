package dev.maicra.eruruupatch.client.crafting;

import dev.maicra.eruruupatch.crafting.CraftingRecipeCandidate;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.neoforged.fml.ModList;

final class RecipeConflictWidget {
    static final int MAX_VISIBLE_ROWS = 8;
    private static final int BUTTON_SIZE = 20;
    private static final int RESULT_SLOT_SIZE = 18;
    private static final int BUTTON_GAP = 7;
    private static final int ROW_HEIGHT = 20;
    private static final int POPUP_PADDING = 2;
    private static final int POPUP_MIN_WIDTH = 160;
    private static final int POPUP_MAX_WIDTH = 280;
    private static final float BUTTON_ICON_SCALE = 1.84F;
    private static final int BUTTON_Z = 20;
    private static final int POPUP_Z = 500;

    private RecipeConflictWidget() {
    }

    static Layout layout(
            AbstractContainerScreen<?> screen,
            CraftingConflictClientState.Snapshot snapshot
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Slot resultSlot = snapshot.menu().getSlot(snapshot.recipeBookMenu().getResultSlotIndex());
        int resultX = screen.getGuiLeft() + resultSlot.x;
        int resultY = screen.getGuiTop() + resultSlot.y;

        int guiRight = screen.getGuiLeft() + screen.getXSize();
        int buttonX = resultX + RESULT_SLOT_SIZE + BUTTON_GAP;
        if (buttonX + BUTTON_SIZE > guiRight - 4) {
            buttonX = resultX - BUTTON_SIZE - BUTTON_GAP;
        }
        int buttonY = resultY + (RESULT_SLOT_SIZE - BUTTON_SIZE) / 2;

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int popupWidth = calculatePopupWidth(minecraft, snapshot, screenWidth);

        // Align the popup's right edge with the button, then allow it to extend
        // outside the vanilla GUI if needed. Long translated item/mod names should
        // remain readable instead of being clipped merely to the container width.
        int popupX = buttonX + BUTTON_SIZE - popupWidth;
        popupX = clamp(popupX, 4, Math.max(4, screenWidth - popupWidth - 4));

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


        int buttonLeft = layout.buttonX();
        int buttonTop = layout.buttonY();

        // The button itself should behave like a normal small control and must not sit
        // above unrelated JEI/EMI or slot tooltips. Keep it on a light foreground layer.
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, BUTTON_Z);
        try {
            renderButtonFrame(graphics, buttonLeft, buttonTop, buttonHovered);
            renderSwitchIcon(graphics, minecraft, buttonLeft, buttonTop);
        } finally {
            graphics.pose().popPose();
        }

        if (!snapshot.popupOpen()) {
            return;
        }

        // The popup itself must still render above slots/items so the choice list remains
        // readable and clickable.
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, POPUP_Z);
        try {
            graphics.fill(
                    layout.popupX(),
                    layout.popupY(),
                    layout.popupX() + layout.popupWidth(),
                    layout.popupY() + layout.popupHeight(),
                    0xf2202020
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
                    graphics.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT, 0xcc4a6b8a);
                } else if (hovered) {
                    graphics.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT, 0xaa5f5f5f);
                }

                graphics.renderItem(candidate.output(), rowX + 2, rowY + 2);
                renderCandidateLabel(graphics, minecraft, candidate, rowX, rowY, rowWidth);

                if (selected) {
                    graphics.drawString(minecraft.font, ">", rowX + rowWidth - 8, rowY + 6, 0xffffffff, false);
                }

                if (hovered) {
                    graphics.renderTooltip(minecraft.font, candidate.output(), mouseX, mouseY);
                }
            }
        } finally {
            graphics.pose().popPose();
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

    private static void renderSwitchIcon(GuiGraphics graphics, Minecraft minecraft, int buttonLeft, int buttonTop) {
        Component symbol = Component.literal("⇄");

        // The swap glyph is far more symmetrical than the refresh icon, but in the
        // Minecraft font it still reads a touch high/left once scaled. Nudge it slightly
        // down and to the right so the visible whitespace is balanced inside the 20x20
        // button.
        float centerX = buttonLeft + BUTTON_SIZE / 2.0F + 1.1F;
        float centerY = buttonTop + BUTTON_SIZE / 2.0F + 1.25F;

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 1.0F);
        graphics.pose().scale(BUTTON_ICON_SCALE, BUTTON_ICON_SCALE, 1.0F);
        float symbolWidth = minecraft.font.width(symbol);
        float symbolHeight = minecraft.font.lineHeight;
        graphics.drawString(
                minecraft.font,
                symbol,
                Math.round(-symbolWidth / 2.0F),
                Math.round(-symbolHeight / 2.0F),
                0xfff4f4f4,
                false
        );
        graphics.pose().popPose();
    }

    private static void renderCandidateLabel(
            GuiGraphics graphics,
            Minecraft minecraft,
            CraftingRecipeCandidate candidate,
            int rowX,
            int rowY,
            int rowWidth
    ) {
        String itemName = candidate.output().getHoverName().getString();
        String modName = modDisplayName(candidate.id());
        String separator = " — ";
        int textX = rowX + 22;
        int maxTextWidth = rowWidth - 34;

        int desiredWidth = minecraft.font.width(itemName)
                + minecraft.font.width(separator)
                + minecraft.font.width(modName);

        if (desiredWidth <= maxTextWidth) {
            graphics.drawString(minecraft.font, itemName, textX, rowY + 6, 0xffffffff, false);
            int modX = textX + minecraft.font.width(itemName);
            graphics.drawString(minecraft.font, separator + modName, modX, rowY + 6, 0xffa8a8a8, false);
            return;
        }

        // Item name always gets priority. Only the trailing mod name is clipped when
        // the screen itself is too narrow to display the complete label.
        String visibleItem = minecraft.font.plainSubstrByWidth(itemName, maxTextWidth);
        graphics.drawString(minecraft.font, visibleItem, textX, rowY + 6, 0xffffffff, false);
        int usedWidth = minecraft.font.width(visibleItem);
        int remaining = maxTextWidth - usedWidth;
        if (remaining > minecraft.font.width(separator) + 4) {
            String trailing = minecraft.font.plainSubstrByWidth(separator + modName, remaining);
            graphics.drawString(minecraft.font, trailing, textX + usedWidth, rowY + 6, 0xffa8a8a8, false);
        }
    }

    private static void renderButtonFrame(GuiGraphics graphics, int buttonLeft, int buttonTop, boolean hovered) {
        int buttonRight = buttonLeft + BUTTON_SIZE;
        int buttonBottom = buttonTop + BUTTON_SIZE;

        int outer = hovered ? 0xffc6c6c6 : 0xffb8b8b8;
        int inner = hovered ? 0xff8f8f8f : 0xff7f7f7f;
        int face = hovered ? 0xffe3e3e3 : 0xffd2d2d2;
        int faceInset = hovered ? 0xffcfcfcf : 0xffbebebe;

        graphics.fill(buttonLeft, buttonTop, buttonRight, buttonBottom, inner);
        graphics.fill(buttonLeft, buttonTop, buttonRight, buttonTop + 1, outer);
        graphics.fill(buttonLeft, buttonTop, buttonLeft + 1, buttonBottom, outer);
        graphics.fill(buttonLeft + 1, buttonTop + 1, buttonRight - 1, buttonBottom - 1, face);
        graphics.fill(buttonLeft + 2, buttonTop + 2, buttonRight - 2, buttonBottom - 2, faceInset);
        graphics.fill(buttonLeft + 1, buttonBottom - 2, buttonRight - 1, buttonBottom - 1, inner);
        graphics.fill(buttonRight - 2, buttonTop + 1, buttonRight - 1, buttonBottom - 1, inner);
    }

    private static int calculatePopupWidth(
            Minecraft minecraft,
            CraftingConflictClientState.Snapshot snapshot,
            int screenWidth
    ) {
        int contentWidth = 0;
        for (CraftingRecipeCandidate candidate : snapshot.choices()) {
            String itemName = candidate.output().getHoverName().getString();
            String modName = modDisplayName(candidate.id());
            int labelWidth = minecraft.font.width(itemName + " — " + modName);
            contentWidth = Math.max(contentWidth, labelWidth);
        }

        // 22 px for item icon/spacing, 12 px for the selected marker and borders.
        int desired = contentWidth + 34;
        int screenLimit = Math.max(POPUP_MIN_WIDTH, screenWidth - 8);
        return Math.min(Math.min(Math.max(POPUP_MIN_WIDTH, desired), POPUP_MAX_WIDTH), screenLimit);
    }

    private static String modDisplayName(ResourceLocation recipeId) {
        String namespace = recipeId.getNamespace();
        if ("minecraft".equals(namespace)) {
            return "Minecraft";
        }
        return ModList.get()
                .getModContainerById(namespace)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(namespace);
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
