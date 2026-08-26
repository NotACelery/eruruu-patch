package dev.maicra.eruruupatch.integration;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public final class RecipeViewerData {
    public static final List<WorldInteractionInfo> WORLD_INTERACTIONS = List.of(
            new WorldInteractionInfo(
                    id("fertilizer_on_dirt"),
                    new ItemStack(Items.DIRT),
                    new ItemStack(ModItems.FERTILIZER.get()),
                    new ItemStack(Items.GRASS_BLOCK),
                    Component.translatable("eruruu_patch.viewer.fertilizer"),
                    1.0F
            ),
            new WorldInteractionInfo(
                    id("wild_cabbage_from_bonemeal"),
                    new ItemStack(Items.GRASS_BLOCK),
                    new ItemStack(Items.BONE_MEAL),
                    stack("farmersdelight", "wild_cabbages"),
                    Component.translatable("eruruu_patch.viewer.bonemeal_surface"),
                    0.08F
            ),
            new WorldInteractionInfo(
                    id("wild_onion_from_bonemeal"),
                    new ItemStack(Items.GRASS_BLOCK),
                    new ItemStack(Items.BONE_MEAL),
                    stack("farmersdelight", "wild_onions"),
                    Component.translatable("eruruu_patch.viewer.bonemeal_surface"),
                    0.08F
            ),
            new WorldInteractionInfo(
                    id("wild_tomato_from_bonemeal"),
                    new ItemStack(Items.GRASS_BLOCK),
                    new ItemStack(Items.BONE_MEAL),
                    stack("farmersdelight", "wild_tomatoes"),
                    Component.translatable("eruruu_patch.viewer.bonemeal_surface"),
                    0.08F
            ),
            new WorldInteractionInfo(
                    id("wild_rice_from_bonemeal"),
                    new ItemStack(Items.DIRT),
                    new ItemStack(Items.BONE_MEAL),
                    stack("farmersdelight", "wild_rice"),
                    Component.translatable("eruruu_patch.viewer.bonemeal_rice"),
                    0.08F
            ),
            new WorldInteractionInfo(
                    id("brown_mushroom_from_damp_room"),
                    new ItemStack(Items.DIRT),
                    new ItemStack(Items.BONE_MEAL),
                    new ItemStack(Items.BROWN_MUSHROOM),
                    Component.translatable("eruruu_patch.viewer.mushroom_room"),
                    0.05F
            ),
            new WorldInteractionInfo(
                    id("red_mushroom_from_damp_room"),
                    new ItemStack(Items.DIRT),
                    new ItemStack(Items.BONE_MEAL),
                    new ItemStack(Items.RED_MUSHROOM),
                    Component.translatable("eruruu_patch.viewer.mushroom_room"),
                    0.05F
            ),
            new WorldInteractionInfo(
                    id("crimson_culture_on_netherrack"),
                    new ItemStack(Items.NETHERRACK),
                    new ItemStack(ModItems.CRIMSON_CULTURE.get()),
                    new ItemStack(Items.CRIMSON_NYLIUM),
                    Component.translatable("eruruu_patch.viewer.nylium_culture"),
                    1.0F
            ),
            new WorldInteractionInfo(
                    id("warped_culture_on_netherrack"),
                    new ItemStack(Items.NETHERRACK),
                    new ItemStack(ModItems.WARPED_CULTURE.get()),
                    new ItemStack(Items.WARPED_NYLIUM),
                    Component.translatable("eruruu_patch.viewer.nylium_culture"),
                    1.0F
            ),
            new WorldInteractionInfo(
                    id("moss_helmet_cobblestone"),
                    new ItemStack(Items.COBBLESTONE),
                    new ItemStack(Items.BONE_MEAL),
                    new ItemStack(Items.MOSS_BLOCK),
                    Component.translatable("eruruu_patch.viewer.moss_helmet"),
                    1.0F
            )
    );


    public static final List<MobDropInfo> MOB_DROPS = List.of(
            new MobDropInfo(
                    id("zombie_gold"),
                    new ItemStack(Items.ZOMBIE_SPAWN_EGG),
                    new ItemStack(Items.GOLD_INGOT),
                    0.008333333F,
                    0.011666667F,
                    0.003333333F,
                    true,
                    true
            ),
            new MobDropInfo(
                    id("witch_nether_wart"),
                    new ItemStack(Items.WITCH_SPAWN_EGG),
                    new ItemStack(Items.NETHER_WART),
                    0.10F,
                    0.125F,
                    0.025F,
                    false,
                    true
            ),
            new MobDropInfo(
                    id("witch_blaze_rod"),
                    new ItemStack(Items.WITCH_SPAWN_EGG),
                    new ItemStack(Items.BLAZE_ROD),
                    0.05F,
                    0.0625F,
                    0.0125F,
                    false,
                    true
            )
    );

    private RecipeViewerData() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, path);
    }

    private static ItemStack stack(String namespace, String path) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        return new ItemStack(item);
    }

    public static String percent(float value) {
        float percentage = value * 100.0F;
        if (percentage < 2.0F) {
            return String.format(java.util.Locale.ROOT, "%.3f%%", percentage);
        }
        if (Math.abs(percentage - Math.round(percentage)) < 0.0001F) {
            return String.format(java.util.Locale.ROOT, "%.0f%%", percentage);
        }
        return String.format(java.util.Locale.ROOT, "%.2f%%", percentage);
    }
}
