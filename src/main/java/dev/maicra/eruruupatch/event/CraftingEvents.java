package dev.maicra.eruruupatch.event;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.recipe.EndlessCharcoalRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Completes the 64-charcoal cost of the Endless Charcoal special recipe.
 *
 * ItemCraftedEvent fires before vanilla removes one ingredient from each
 * occupied crafting slot, so the extra consumption is queued until the end
 * of the server tick. At that point vanilla has already performed its normal
 * ingredient removal and we consume the remainder required to reach 64.
 */
public final class CraftingEvents {
    private static final Queue<PendingConsumption> PENDING = new ArrayDeque<>();

    private CraftingEvents() {
    }

    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!event.getCrafting().is(ModItems.ENDLESS_CHARCOAL.get())) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Container grid = event.getInventory();
        int occupiedCharcoalSlots = 0;
        int totalCharcoal = 0;

        for (int i = 0; i < grid.getContainerSize(); i++) {
            ItemStack stack = grid.getItem(i);
            if (!stack.isEmpty()) {
                if (!stack.is(Items.CHARCOAL)) {
                    return;
                }
                occupiedCharcoalSlots++;
                totalCharcoal += stack.getCount();
            }
        }

        if (totalCharcoal < EndlessCharcoalRecipe.REQUIRED_CHARCOAL || occupiedCharcoalSlots == 0) {
            return;
        }

        // Vanilla will consume one charcoal from every occupied slot after
        // this event. Queue only the additional amount needed to total 64.
        int extraToConsume = EndlessCharcoalRecipe.REQUIRED_CHARCOAL - occupiedCharcoalSlots;
        if (extraToConsume > 0) {
            PENDING.add(new PendingConsumption(player, grid, extraToConsume));
        }
    }

    public static void onServerTickPost(ServerTickEvent.Post event) {
        int pendingCount = PENDING.size();
        for (int n = 0; n < pendingCount; n++) {
            PendingConsumption pending = PENDING.poll();
            if (pending == null) {
                return;
            }

            if (pending.player().server != event.getServer()) {
                PENDING.add(pending);
                continue;
            }

            int remaining = consumeCharcoal(pending.grid(), pending.amount());

            // Normally everything is still in the crafting grid. This
            // fallback also covers a player moving the leftovers to their
            // inventory before the end-of-tick cleanup occurs.
            if (remaining > 0) {
                consumeCharcoal(pending.player().getInventory(), remaining);
            }
        }
    }

    private static int consumeCharcoal(Container container, int amount) {
        int remaining = amount;
        for (int i = 0; i < container.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.is(Items.CHARCOAL)) {
                continue;
            }

            int removeNow = Math.min(stack.getCount(), remaining);
            stack.shrink(removeNow);
            remaining -= removeNow;
        }
        container.setChanged();
        return remaining;
    }

    private record PendingConsumption(ServerPlayer player, Container grid, int amount) {
    }
}
