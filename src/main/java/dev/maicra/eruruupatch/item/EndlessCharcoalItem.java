package dev.maicra.eruruupatch.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A non-stackable charcoal item with a permanent enchantment glint.
 * Its furnace burn time is supplied by NeoForge's furnace_fuels data map.
 */
public final class EndlessCharcoalItem extends Item {
    public EndlessCharcoalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
