package dev.maicra.eruruupatch.integration.jade;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

/**
 * Server-backed Cutter information for Jade.
 *
 * Variant and equipped tool are normal text lines. Finished products are sent
 * explicitly from the four output slots and rendered with the same public Jade
 * elements used by the universal item-storage display: small item icon followed
 * by "amount x item name". Inputs and the protected tool slot are never included
 * in the product list.
 */
public enum CutterJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("eruruu_patch", "cutter_info");
    private static final String KEY_VARIANT = "Variant";
    private static final String KEY_TOOL = "Tool";
    private static final String KEY_OUTPUTS = "Outputs";
    private static final String KEY_STACK = "Stack";
    private static final String KEY_COUNT = "Count";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof CutterBlockEntity cutter)) {
            return;
        }

        data.putString(KEY_VARIANT, BuiltInRegistries.BLOCK.getKey(cutter.logVariant()).toString());

        data.remove(KEY_TOOL);
        ItemStack equipped = cutter.toolHandler().getStackInSlot(0);
        if (!equipped.isEmpty()) {
            data.putString(KEY_TOOL, BuiltInRegistries.ITEM.getKey(equipped.getItem()).toString());
        }

        data.remove(KEY_OUTPUTS);
        List<OutputEntry> outputs = aggregateOutputs(cutter);
        if (!outputs.isEmpty()) {
            ListTag list = new ListTag();
            for (OutputEntry entry : outputs) {
                CompoundTag serialized = new CompoundTag();
                serialized.put(KEY_STACK, entry.stack().save(accessor.getLevel().registryAccess()));
                serialized.putInt(KEY_COUNT, entry.count());
                list.add(serialized);
            }
            data.put(KEY_OUTPUTS, list);
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        String variantId = data.getString(KEY_VARIANT);
        if (!variantId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(variantId);
            Block block = id == null ? null : BuiltInRegistries.BLOCK.get(id);
            if (block != null) {
                tooltip.add(Component.translatable(
                        "jade.eruruu_patch.cutter.variant",
                        Component.translatable(CutterLogVariant.translationKey(block))
                ));
            }
        }

        String toolId = data.getString(KEY_TOOL);
        if (!toolId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(toolId);
            Item item = id == null ? null : BuiltInRegistries.ITEM.get(id);
            if (item != null) {
                tooltip.add(Component.translatable(
                        "jade.eruruu_patch.cutter.tool",
                        new ItemStack(item).getHoverName()
                ));
            }
        }

        appendOutputLines(tooltip, accessor, data);
    }

    private static void appendOutputLines(ITooltip tooltip, BlockAccessor accessor, CompoundTag data) {
        if (!data.contains(KEY_OUTPUTS, Tag.TAG_LIST)) {
            return;
        }

        ListTag outputs = data.getList(KEY_OUTPUTS, Tag.TAG_COMPOUND);
        IElementHelper elements = IElementHelper.get();
        IDisplayHelper display = IDisplayHelper.get();

        for (int i = 0; i < outputs.size(); i++) {
            CompoundTag serialized = outputs.getCompound(i);
            ItemStack stack = ItemStack.parseOptional(
                    accessor.getLevel().registryAccess(),
                    serialized.getCompound(KEY_STACK)
            );
            int count = Math.max(0, serialized.getInt(KEY_COUNT));
            if (stack.isEmpty() || count <= 0) {
                continue;
            }

            String amount = display.humanReadableNumber(count, "", false, null);
            Component line = Component.literal(amount)
                    .append("× ")
                    .append(display.stripColor(stack.getHoverName()));

            // This deliberately mirrors Jade's universal item-storage row:
            // small icon + amount x stripped item name.
            List<IElement> row = List.of(
                    elements.smallItem(stack).clearCachedMessage(),
                    elements.text(line).message(null)
            );
            tooltip.add(row);
        }
    }

    private static List<OutputEntry> aggregateOutputs(CutterBlockEntity cutter) {
        List<OutputEntry> result = new ArrayList<>();
        for (int slot = 0; slot < cutter.outputHandler().getSlots(); slot++) {
            ItemStack stack = cutter.outputHandler().getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            boolean merged = false;
            for (int i = 0; i < result.size(); i++) {
                OutputEntry existing = result.get(i);
                if (ItemStack.isSameItemSameComponents(existing.stack(), stack)) {
                    result.set(i, new OutputEntry(existing.stack(), existing.count() + stack.getCount()));
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                result.add(new OutputEntry(stack.copyWithCount(1), stack.getCount()));
            }
        }
        return result;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    private record OutputEntry(ItemStack stack, int count) {
    }
}
