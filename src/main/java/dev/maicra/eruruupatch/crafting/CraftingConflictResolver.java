package dev.maicra.eruruupatch.crafting;

import dev.maicra.eruruupatch.network.SelectCraftingRecipePayload;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Generic resolver for crafting recipes that match the exact same current grid.
 *
 * The client may enumerate candidates for presentation, but only this server
 * state is allowed to choose the authoritative recipe/result.
 */
public final class CraftingConflictResolver {
    private static final Map<ServerPlayer, CraftingConflictState> SERVER_STATES = new WeakHashMap<>();

    private CraftingConflictResolver() {
    }

    public static List<CraftingRecipeCandidate> findCandidates(Level level, CraftingInput input) {
        if (input.isEmpty()) {
            return List.of();
        }

        List<CraftingRecipeCandidate> candidates = new ArrayList<>();
        for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager()
                .getRecipesFor(RecipeType.CRAFTING, input, level)) {
            ItemStack output = holder.value().assemble(input, level.registryAccess());
            if (output.isEmpty()) {
                continue;
            }

            NonNullList<ItemStack> remainders = holder.value().getRemainingItems(input);
            candidates.add(new CraftingRecipeCandidate(holder, output, remainders));
        }

        candidates.sort(Comparator.comparing(candidate -> candidate.id().toString()));
        return List.copyOf(candidates);
    }

    /**
     * Returns one representative for every semantically distinct choice.
     * Exact duplicate recipes (same output and same remainders) do not create a
     * redundant selector row, while recipes with different container/remainder
     * behavior remain selectable even if their visible output is identical.
     */
    public static List<CraftingRecipeCandidate> distinctChoices(List<CraftingRecipeCandidate> candidates) {
        List<CraftingRecipeCandidate> distinct = new ArrayList<>();
        outer:
        for (CraftingRecipeCandidate candidate : candidates) {
            for (CraftingRecipeCandidate existing : distinct) {
                if (sameSemantics(candidate, existing)) {
                    continue outer;
                }
            }
            distinct.add(candidate);
        }
        return List.copyOf(distinct);
    }

    public static void onCraftingGridChanged(
            AbstractContainerMenu menu,
            Level level,
            Player player,
            CraftingContainer craftSlots,
            ResultContainer resultSlots,
            @Nullable RecipeHolder<CraftingRecipe> vanillaRecipe
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // InventoryMenu remains subscribed even while another container is open.
        // Never let the background 2x2 grid overwrite an active 3x3 selection.
        if (serverPlayer.containerMenu != menu) {
            return;
        }

        CraftingInput input = craftSlots.asCraftInput();
        long signature = CraftingInputSignature.of(input);
        List<CraftingRecipeCandidate> candidates = findCandidates(level, input);
        List<CraftingRecipeCandidate> distinct = distinctChoices(candidates);

        CraftingConflictState state = SERVER_STATES.computeIfAbsent(serverPlayer, ignored -> new CraftingConflictState());
        boolean changedMenu = !state.belongsTo(menu);
        boolean changedInput = changedMenu || state.signature != signature;
        ResourceLocation previousSelection = changedMenu ? null : state.selectedRecipeId;
        if (changedInput) {
            state.reset(menu, craftSlots, resultSlots, signature);
        } else {
            state.craftSlots = craftSlots;
            state.resultSlots = resultSlots;
        }

        state.semanticCandidateCount = distinct.size();
        if (distinct.size() < 2) {
            state.selectedRecipeId = null;
            state.selectedRecipe = null;
            return;
        }

        CraftingRecipeCandidate selected = null;
        ResourceLocation selectionToKeep = changedInput ? previousSelection : state.selectedRecipeId;
        if (selectionToKeep != null) {
            selected = byId(candidates, selectionToKeep).orElse(null);
        }

        if (selected == null && vanillaRecipe != null) {
            selected = byId(candidates, vanillaRecipe.id()).orElse(null);
        }
        if (selected == null && !candidates.isEmpty()) {
            selected = candidates.getFirst();
        }
        if (selected == null) {
            state.selectedRecipeId = null;
            state.selectedRecipe = null;
            return;
        }

        state.selectedRecipeId = selected.id();
        state.selectedRecipe = selected.recipe();
        applySelection(menu, resultSlots, selected);
    }

    public static void handleSelection(ServerPlayer player, SelectCraftingRecipePayload payload) {
        CraftingConflictState state = SERVER_STATES.get(player);
        if (state == null || !state.belongsTo(player.containerMenu)) {
            return;
        }
        if (player.containerMenu.containerId != payload.containerId()) {
            return;
        }
        if (state.craftSlots == null || state.resultSlots == null) {
            return;
        }

        CraftingInput input = state.craftSlots.asCraftInput();
        long currentSignature = CraftingInputSignature.of(input);
        if (currentSignature != payload.inputSignature() || currentSignature != state.signature) {
            return;
        }

        List<CraftingRecipeCandidate> candidates = findCandidates(player.level(), input);
        if (distinctChoices(candidates).size() < 2) {
            return;
        }

        CraftingRecipeCandidate requested = byId(candidates, payload.recipeId()).orElse(null);
        if (requested == null) {
            return;
        }

        // Re-check the concrete recipe against the current grid at request time;
        // the client-supplied id is never trusted as an ItemStack/result source.
        if (!requested.recipe().value().matches(input, player.level())) {
            return;
        }

        state.selectedRecipeId = requested.id();
        state.selectedRecipe = requested.recipe();
        state.semanticCandidateCount = distinctChoices(candidates).size();
        applySelection(player.containerMenu, state.resultSlots, requested);
    }

    public static @Nullable NonNullList<ItemStack> selectedRemainders(
            ServerPlayer player,
            CraftingInput positionedInput,
            Level level
    ) {
        CraftingConflictState state = SERVER_STATES.get(player);
        if (state == null
                || !state.belongsTo(player.containerMenu)
                || state.semanticCandidateCount < 2
                || state.selectedRecipe == null) {
            return null;
        }

        CraftingRecipe recipe = state.selectedRecipe.value();
        if (!recipe.matches(positionedInput, level)) {
            return null;
        }
        return recipe.getRemainingItems(positionedInput);
    }

    private static Optional<CraftingRecipeCandidate> byId(
            List<CraftingRecipeCandidate> candidates,
            ResourceLocation id
    ) {
        return candidates.stream().filter(candidate -> candidate.id().equals(id)).findFirst();
    }

    private static void applySelection(
            AbstractContainerMenu menu,
            ResultContainer resultSlots,
            CraftingRecipeCandidate selected
    ) {
        ItemStack output = selected.output().copy();
        resultSlots.setRecipeUsed(selected.recipe());
        resultSlots.setItem(0, output);

        // Vanilla may already have synchronised its default candidate in the same
        // grid-change call. Broadcast once more so the authoritative alternative
        // becomes the final slot state on the client.
        menu.broadcastChanges();
    }

    private static boolean sameSemantics(CraftingRecipeCandidate left, CraftingRecipeCandidate right) {
        if (!ItemStack.matches(left.output(), right.output())) {
            return false;
        }
        if (left.remainders().size() != right.remainders().size()) {
            return false;
        }
        for (int index = 0; index < left.remainders().size(); index++) {
            if (!ItemStack.matches(left.remainders().get(index), right.remainders().get(index))) {
                return false;
            }
        }
        return true;
    }
}
