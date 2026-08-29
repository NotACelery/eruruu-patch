package dev.maicra.eruruupatch.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class DenseCharcoalItem extends Item {
    public DenseCharcoalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
