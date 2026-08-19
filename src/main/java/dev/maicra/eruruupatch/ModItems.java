package dev.maicra.eruruupatch;

import dev.maicra.eruruupatch.item.EndlessCharcoalItem;
import dev.maicra.eruruupatch.item.CutterItem;
import dev.maicra.eruruupatch.item.FertilizerItem;
import dev.maicra.eruruupatch.item.NyliumCultureItem;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final int HASTE_DURATION_TICKS = 20 * 60 * 10;

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, EruruuPatch.MOD_ID);

    public static final DeferredHolder<Item, Item> FERTILIZER =
            ITEMS.register("fertilizer", () -> new FertilizerItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ENDLESS_CHARCOAL =
            ITEMS.register("endless_charcoal", () -> new EndlessCharcoalItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> CRIMSON_CULTURE =
            ITEMS.register("crimson_culture", () -> new NyliumCultureItem(new Item.Properties(), Blocks.CRIMSON_NYLIUM));

    public static final DeferredHolder<Item, Item> WARPED_CULTURE =
            ITEMS.register("warped_culture", () -> new NyliumCultureItem(new Item.Properties(), Blocks.WARPED_NYLIUM));

    /**
     * Early-game moss starter. It deliberately uses vanilla leather armor behavior
     * and a fixed green dyed-color component so it looks like a green leather helmet.
     */
    public static final DeferredHolder<Item, Item> MOSS_HELMET =
            ITEMS.register("moss_helmet", () -> new ArmorItem(
                    ArmorMaterials.LEATHER,
                    ArmorItem.Type.HELMET,
                    new Item.Properties().component(
                            DataComponents.DYED_COLOR,
                            new DyedItemColor(0x5E7C16, false)
                    )
            ));

    /**
     * A compact crafting ingredient made from four Sugar. It intentionally behaves
     * as an item rather than a placed block; its item model looks like White Wool.
     */
    public static final DeferredHolder<Item, Item> SUGAR_BLOCK =
            ITEMS.register("sugar_block", () -> new Item(new Item.Properties()));

    /**
     * AFK mining food: 2 hunger, 3 saturation and ten minutes of Haste I.
     */
    public static final DeferredHolder<Item, Item> SUGAR_PICKAXE =
            ITEMS.register("sugar_pickaxe", () -> new Item(hasteFoodProperties(0, false,
                    "tooltip.eruruu_patch.sugar_pickaxe")));

    /**
     * Expensive AFK mining food: the same nutrition, but ten minutes of Haste II.
     * The glint is forced so the composite Sugar + Wooden Pickaxe icon visibly
     * distinguishes the concentrated recipe.
     */
    public static final DeferredHolder<Item, Item> ENCHANTED_SUGAR_PICKAXE =
            ITEMS.register("enchanted_sugar_pickaxe", () -> new Item(hasteFoodProperties(1, true,
                    "tooltip.eruruu_patch.enchanted_sugar_pickaxe")));

    /**
     * Purely visual item used by the Eruruu Patch creative tab as its icon.
     * It intentionally has no recipe and is not listed inside the tab itself.
     */
    public static final DeferredHolder<Item, Item> ERURUU_ICON =
            ITEMS.register("eruruu_icon", () -> new Item(new Item.Properties().stacksTo(1)));

    /** Laboratory Cutter block item. Contents persist through BLOCK_ENTITY_DATA. */
    public static final DeferredHolder<Item, Item> CUTTER =
            ITEMS.register("cutter", () -> new CutterItem(
                    dev.maicra.eruruupatch.registry.ModBlocks.CUTTER.get(),
                    new Item.Properties()
            ));

    private static Item.Properties hasteFoodProperties(int amplifier, boolean glint, String tooltipKey) {
        // nutrition=2 restores two hunger points. A 0.75 saturation modifier gives
        // exactly 3 saturation: 2 * nutrition * modifier = 3.
        FoodProperties food = new FoodProperties.Builder()
                .nutrition(2)
                .saturationModifier(0.75F)
                .alwaysEdible()
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, HASTE_DURATION_TICKS, amplifier), 1.0F)
                .build();

        Item.Properties properties = new Item.Properties()
                .food(food)
                .component(DataComponents.LORE, new ItemLore(List.of(
                        Component.translatable(tooltipKey),
                        Component.translatable("tooltip.eruruu_patch.sugar_pickaxe_nutrition")
                )));

        if (glint) {
            properties.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return properties;
    }

    private ModItems() {
    }
}
