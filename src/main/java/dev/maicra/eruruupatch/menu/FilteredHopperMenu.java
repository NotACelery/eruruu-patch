package dev.maicra.eruruupatch.menu;

import dev.maicra.eruruupatch.blockentity.FilteredHopperBlockEntity;
import dev.maicra.eruruupatch.registry.ModBlocks;
import dev.maicra.eruruupatch.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FilteredHopperMenu extends AbstractContainerMenu {
    public static final int STORAGE_START = 0;
    public static final int STORAGE_END = 5;
    public static final int FILTER_SLOT = 5;
    public static final int PLAYER_START = 6;
    public static final int PLAYER_END = 42;

    private final BlockPos blockPos;
    private final Container storage;
    private final Container filterContainer;

    public FilteredHopperMenu(int id, Inventory inventory, FilteredHopperBlockEntity hopper) {
        this(id, inventory, hopper.getBlockPos(), hopper, hopper.getFilterContainer());
    }

    private FilteredHopperMenu(int id, Inventory inventory, BlockPos pos, Container storage, Container filterContainer) {
        super(ModMenus.FILTERED_HOPPER, id);
        this.blockPos = pos;
        this.storage = storage;
        this.filterContainer = filterContainer;
        checkContainerSize(storage, FilteredHopperBlockEntity.STORAGE_SIZE);
        checkContainerSize(filterContainer, 1);
        storage.startOpen(inventory.player);

        for (int slot = 0; slot < FilteredHopperBlockEntity.STORAGE_SIZE; slot++) {
            addSlot(new FilteredStorageSlot(storage, slot, 44 + slot * 18, 20));
        }
        addSlot(new FilterSlot(filterContainer, 0, 152, 20));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 51 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 109));
        }
    }

    public static FilteredHopperMenu fromNetwork(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
        if (blockEntity instanceof FilteredHopperBlockEntity hopper) {
            return new FilteredHopperMenu(id, inventory, hopper);
        }
        return new FilteredHopperMenu(id, inventory, pos, new SimpleContainer(5), new SimpleContainer(1));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot source = slots.get(index);
        if (!source.hasItem()) return ItemStack.EMPTY;

        ItemStack moving = source.getItem();
        ItemStack original = moving.copy();

        if (index < PLAYER_START) {
            if (!moveItemStackTo(moving, PLAYER_START, PLAYER_END, true)) return ItemStack.EMPTY;
        } else {
            // Shift-click never configures the filter. It is deliberately a direct-click-only slot.
            if (!accepts(moving) || !moveItemStackTo(moving, STORAGE_START, STORAGE_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (moving.isEmpty()) source.setByPlayer(ItemStack.EMPTY);
        else source.setChanged();
        return original;
    }

    private boolean accepts(ItemStack stack) {
        ItemStack filter = filterContainer.getItem(0);
        return filter.isEmpty() || stack.is(filter.getItem());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        storage.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) > 64.0) {
            return false;
        }
        return player.level().getBlockState(blockPos).is(ModBlocks.FILTERED_HOPPER.get());
    }

    private final class FilteredStorageSlot extends Slot {
        private FilteredStorageSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return accepts(stack) && super.mayPlace(stack);
        }
    }

    private static final class FilterSlot extends Slot {
        private FilterSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }
    }
}
