package dev.maicra.eruruupatch.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Final compact charcoal fuel. The registry ID intentionally remains
 * eruruu_patch:endless_charcoal so existing worlds keep their item stacks.
 * Furnace burn time is supplied by NeoForge's furnace_fuels data map.
 */
public final class DenseCharcoalItem extends Item {
    public DenseCharcoalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
