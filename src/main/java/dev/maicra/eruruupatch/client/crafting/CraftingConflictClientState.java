package dev.maicra.eruruupatch.client.crafting;

import dev.maicra.eruruupatch.crafting.CraftingConflictResolver;
import dev.maicra.eruruupatch.crafting.CraftingInputSignature;
import dev.maicra.eruruupatch.crafting.CraftingRecipeCandidate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

final class CraftingConflictClientState {
    private AbstractContainerMenu menu;
    private long selectionSignature = Long.MIN_VALUE;
    private long evaluationSignature = Long.MIN_VALUE;
    private List<CraftingRecipeCandidate> choices = List.of();
    private ResourceLocation selectedRecipeId;
    private boolean popupOpen;
    private int scrollOffset;

    Snapshot refresh(AbstractContainerScreen<?> screen) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            clear();
            return null;
        }

        AbstractContainerMenu currentMenu = screen.getMenu();
        if (!(currentMenu instanceof CraftingMenu) && !(currentMenu instanceof InventoryMenu)) {
            clear();
            return null;
        }
        if (!(currentMenu instanceof RecipeBookMenu<?, ?> recipeBookMenu)) {
            clear();
            return null;
        }

        int width = recipeBookMenu.getGridWidth();
        int height = recipeBookMenu.getGridHeight();
        int resultSlotIndex = recipeBookMenu.getResultSlotIndex();
        int inputCount = width * height;
        if (resultSlotIndex < 0 || resultSlotIndex + inputCount >= currentMenu.slots.size()) {
            clear();
            return null;
        }

        List<ItemStack> stacks = new ArrayList<>(inputCount);
        for (int index = 0; index < inputCount; index++) {
            stacks.add(currentMenu.getSlot(resultSlotIndex + 1 + index).getItem().copy());
        }

        CraftingInput input = CraftingInput.of(width, height, stacks);
        long currentSelectionSignature = CraftingInputSignature.of(input);
        long currentEvaluationSignature = evaluationSignature(input);

        boolean menuChanged = this.menu != currentMenu;
        boolean patternChanged = menuChanged || this.selectionSignature != currentSelectionSignature;
        if (patternChanged) {
            this.menu = currentMenu;
            this.selectionSignature = currentSelectionSignature;
            if (menuChanged) {
                this.selectedRecipeId = null;
            }
            this.popupOpen = false;
            this.scrollOffset = 0;
        }

        if (patternChanged || this.evaluationSignature != currentEvaluationSignature) {
            this.evaluationSignature = currentEvaluationSignature;
            this.choices = CraftingConflictResolver.distinctChoices(
                    CraftingConflictResolver.findCandidates(minecraft.level, input)
            );
            if (this.choices.size() < 2) {
                this.popupOpen = false;
                this.scrollOffset = 0;
            }
        }

        if (selectedRecipeId != null && choices.stream().noneMatch(choice -> choice.id().equals(selectedRecipeId))) {
            selectedRecipeId = null;
        }

        ItemStack result = currentMenu.getSlot(resultSlotIndex).getItem();
        if (selectedRecipeId == null && !result.isEmpty()) {
            for (CraftingRecipeCandidate candidate : choices) {
                if (ItemStack.matches(candidate.output(), result)) {
                    selectedRecipeId = candidate.id();
                    break;
                }
            }
        }

        int maxScroll = Math.max(0, choices.size() - RecipeConflictWidget.MAX_VISIBLE_ROWS);
        scrollOffset = Math.min(scrollOffset, maxScroll);

        return new Snapshot(
                currentMenu,
                recipeBookMenu,
                input,
                selectionSignature,
                choices,
                selectedRecipeId,
                popupOpen,
                scrollOffset
        );
    }

    void togglePopup() {
        if (choices.size() >= 2) {
            popupOpen = !popupOpen;
        }
    }

    void closePopup() {
        popupOpen = false;
    }

    void select(ResourceLocation recipeId) {
        selectedRecipeId = recipeId;
        popupOpen = false;
    }

    void scroll(int delta, int visibleRows) {
        int maxScroll = Math.max(0, choices.size() - visibleRows);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + delta));
    }

    void clear() {
        menu = null;
        selectionSignature = Long.MIN_VALUE;
        evaluationSignature = Long.MIN_VALUE;
        choices = List.of();
        selectedRecipeId = null;
        popupOpen = false;
        scrollOffset = 0;
    }

    private static long evaluationSignature(CraftingInput input) {
        long hash = 0x9e3779b97f4a7c15L;
        hash = mix(hash, input.width());
        hash = mix(hash, input.height());
        for (int index = 0; index < input.size(); index++) {
            ItemStack stack = input.getItem(index);
            hash = mix(hash, index);
            hash = mix(hash, stack.isEmpty() ? 0 : ItemStack.hashItemAndComponents(stack));
            hash = mix(hash, stack.getCount());
        }
        return hash;
    }

    private static long mix(long hash, int value) {
        hash ^= Integer.toUnsignedLong(value) + 0x9e3779b97f4a7c15L + (hash << 6) + (hash >>> 2);
        return hash;
    }

    record Snapshot(
            AbstractContainerMenu menu,
            RecipeBookMenu<?, ?> recipeBookMenu,
            CraftingInput input,
            long signature,
            List<CraftingRecipeCandidate> choices,
            ResourceLocation selectedRecipeId,
            boolean popupOpen,
            int scrollOffset
    ) {
    }
}
