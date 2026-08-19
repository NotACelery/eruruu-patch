package dev.maicra.eruruupatch.world;

import dev.maicra.eruruupatch.event.ReinforcedPickaxeEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;

@EventBusSubscriber(modid = "eruruu_patch")
public final class ReinforcedEnchantingCompat {
    private ReinforcedEnchantingCompat() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void restoreEnchantingTable(EnchantmentLevelSetEvent event) {
        if (ReinforcedPickaxeEvents.isReinforced(event.getItem()) && event.getEnchantLevel() == 0) {
            event.setEnchantLevel(event.getOriginalLevel());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowEnchantedBooks(AnvilUpdateEvent event) {
        if (!ReinforcedPickaxeEvents.isReinforced(event.getLeft())) {
            return;
        }

        ItemStack right = event.getRight();
        ItemEnchantments stored = right.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored != null && !stored.isEmpty()) {
            event.setCanceled(false);
        }
    }
}
