package dev.maicra.eruruupatch.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record MobDropInfo(
        ResourceLocation id,
        ItemStack mobIcon,
        ItemStack output,
        float baseChance,
        float lootingOneChance,
        float perLootingLevelAboveOne,
        boolean requiresPlayerKill,
        boolean easyMobFarmCompatible
) {
    public float chanceAtLooting(int level) {
        if (level <= 0) {
            return baseChance;
        }
        return lootingOneChance + Math.max(0, level - 1) * perLootingLevelAboveOne;
    }
}
