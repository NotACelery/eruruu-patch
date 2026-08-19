package dev.maicra.eruruupatch.compat.easyfarmers;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/** Canonical material-variant encoding for the laboratory Cutter. */
public final class CutterLogVariant {
    public static final String NBT_KEY = "CutterLog";
    public static final List<Block> SUPPORTED = List.of(
            Blocks.OAK_LOG,
            Blocks.SPRUCE_LOG,
            Blocks.BIRCH_LOG,
            Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG,
            Blocks.MANGROVE_LOG,
            Blocks.CHERRY_LOG,
            Blocks.BAMBOO_BLOCK
    );
    public static final TagKey<Item> ALLOWED_LOGS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("eruruu_patch", "cutter_logs")
    );

    private CutterLogVariant() {
    }

    public static boolean isAllowed(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(ALLOWED_LOGS) && stack.getItem() instanceof BlockItem;
    }

    public static Block fromIngredient(ItemStack stack) {
        if (!isAllowed(stack) || !(stack.getItem() instanceof BlockItem blockItem)) {
            return Blocks.OAK_LOG;
        }
        return blockItem.getBlock();
    }

    public static Block fromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Blocks.OAK_LOG;
        }
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data == null ? Blocks.OAK_LOG : read(data.copyTag());
    }

    public static Block read(CompoundTag tag) {
        if (tag == null || !tag.contains(NBT_KEY)) {
            return Blocks.OAK_LOG;
        }
        ResourceLocation id = ResourceLocation.tryParse(tag.getString(NBT_KEY));
        if (id == null) {
            return Blocks.OAK_LOG;
        }
        Block block = BuiltInRegistries.BLOCK.get(id);
        ItemStack asItem = new ItemStack(block.asItem());
        return isAllowed(asItem) ? block : Blocks.OAK_LOG;
    }

    /** Oak is deliberately omitted so legacy/plain Cutter items remain canonical. */
    public static void write(CompoundTag tag, Block log) {
        if (tag == null || log == null || log == Blocks.OAK_LOG) {
            return;
        }
        ItemStack asItem = new ItemStack(log.asItem());
        if (!isAllowed(asItem)) {
            return;
        }
        tag.putString(NBT_KEY, BuiltInRegistries.BLOCK.getKey(log).toString());
    }

    public static ItemStack createCutter(Block log) {
        ItemStack result = new ItemStack(ModItems.CUTTER.get());
        if (log == null || log == Blocks.OAK_LOG) {
            return result;
        }
        CompoundTag tag = new CompoundTag();
        write(tag, log);
        if (!tag.isEmpty()) {
            BlockItem.setBlockEntityData(result, ModBlockEntities.CUTTER.get(), tag);
        }
        return result;
    }

    public static String translationKey(Block log) {
        if (log == Blocks.SPRUCE_LOG) return "variant.eruruu_patch.cutter.spruce";
        if (log == Blocks.BIRCH_LOG) return "variant.eruruu_patch.cutter.birch";
        if (log == Blocks.JUNGLE_LOG) return "variant.eruruu_patch.cutter.jungle";
        if (log == Blocks.ACACIA_LOG) return "variant.eruruu_patch.cutter.acacia";
        if (log == Blocks.DARK_OAK_LOG) return "variant.eruruu_patch.cutter.dark_oak";
        if (log == Blocks.MANGROVE_LOG) return "variant.eruruu_patch.cutter.mangrove";
        if (log == Blocks.CHERRY_LOG) return "variant.eruruu_patch.cutter.cherry";
        if (log == Blocks.BAMBOO_BLOCK) return "variant.eruruu_patch.cutter.bamboo";
        return "variant.eruruu_patch.cutter.oak";
    }
}
