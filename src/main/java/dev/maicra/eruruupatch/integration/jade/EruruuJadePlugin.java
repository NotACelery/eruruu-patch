package dev.maicra.eruruupatch.integration.jade;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.block.FilteredHopperBlock;
import dev.maicra.eruruupatch.blockentity.FilteredHopperBlockEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/** Concise Jade inventory summaries for both vanilla and filtered hoppers. */
@WailaPlugin("jade")
public final class EruruuJadePlugin implements IWailaPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "hopper_summary");
    private static final String ROOT = "EruruuHopperSummary";
    private static final String USED = "Used";
    private static final String FILTER = "Filter";
    private static final String ITEMS = "Items";
    private static final String ID = "Id";
    private static final String COUNT = "Count";
    private static final int MAX_VISIBLE_TYPES = 3;

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(Provider.INSTANCE, HopperBlockEntity.class);
        registration.registerBlockDataProvider(Provider.INSTANCE, FilteredHopperBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(Provider.INSTANCE, HopperBlock.class);
        registration.registerBlockComponent(Provider.INSTANCE, FilteredHopperBlock.class);
    }

    private enum Provider implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof Container container)) return;
            CompoundTag summary = buildSummary(container, accessor.getBlockEntity() instanceof FilteredHopperBlockEntity filtered ? filtered : null);
            data.put(ROOT, summary);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag summary = accessor.getServerData().contains(ROOT)
                    ? accessor.getServerData().getCompound(ROOT)
                    : localSummary(accessor);
            if (summary == null) return;

            if (summary.contains(FILTER)) {
                String filterId = summary.getString(FILTER);
                if (filterId.isEmpty()) {
                    tooltip.add(Component.translatable("jade.eruruu_patch.hopper.filter.none")
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable(
                                    "jade.eruruu_patch.hopper.filter",
                                    itemName(filterId)
                            )
                            .withStyle(ChatFormatting.GOLD));
                }
            }

            int used = summary.getInt(USED);
            ListTag items = summary.getList(ITEMS, Tag.TAG_COMPOUND);
            if (used <= 0 || items.isEmpty()) {
                tooltip.add(Component.translatable("jade.eruruu_patch.hopper.contents.empty")
                        .withStyle(ChatFormatting.GRAY));
                return;
            }

            tooltip.add(Component.translatable(
                            "jade.eruruu_patch.hopper.contents",
                            used,
                            FilteredHopperBlockEntity.STORAGE_SIZE
                    )
                    .withStyle(ChatFormatting.WHITE));

            int visible = Math.min(MAX_VISIBLE_TYPES, items.size());
            for (int i = 0; i < visible; i++) {
                CompoundTag entry = items.getCompound(i);
                tooltip.add(Component.translatable(
                                "jade.eruruu_patch.hopper.item",
                                itemName(entry.getString(ID)),
                                entry.getInt(COUNT)
                        )
                        .withStyle(ChatFormatting.GRAY));
            }
            if (items.size() > visible) {
                tooltip.add(Component.translatable(
                                "jade.eruruu_patch.hopper.more",
                                items.size() - visible
                        )
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    private static CompoundTag localSummary(BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof Container container)) return null;
        return buildSummary(container, accessor.getBlockEntity() instanceof FilteredHopperBlockEntity filtered ? filtered : null);
    }

    private static CompoundTag buildSummary(Container container, FilteredHopperBlockEntity filtered) {
        CompoundTag summary = new CompoundTag();
        int used = 0;
        Map<ResourceLocation, Integer> grouped = new LinkedHashMap<>();
        int slots = Math.min(FilteredHopperBlockEntity.STORAGE_SIZE, container.getContainerSize());
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) continue;
            used++;
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            grouped.merge(id, stack.getCount(), Integer::sum);
        }
        summary.putInt(USED, used);

        ListTag items = new ListTag();
        for (Map.Entry<ResourceLocation, Integer> entry : grouped.entrySet()) {
            CompoundTag item = new CompoundTag();
            item.putString(ID, entry.getKey().toString());
            item.putInt(COUNT, entry.getValue());
            items.add(item);
        }
        summary.put(ITEMS, items);

        if (filtered != null) {
            ItemStack filter = filtered.getFilter();
            summary.putString(FILTER, filter.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(filter.getItem()).toString());
        }
        return summary;
    }

    private static Component itemName(String idString) {
        ResourceLocation id = ResourceLocation.tryParse(idString);
        if (id == null) return Component.literal(idString);
        return BuiltInRegistries.ITEM.getOptional(id)
                .map(item -> item.getDescription())
                .orElseGet(() -> Component.literal(idString));
    }
}
