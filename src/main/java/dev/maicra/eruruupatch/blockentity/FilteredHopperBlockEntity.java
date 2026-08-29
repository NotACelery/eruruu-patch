package dev.maicra.eruruupatch.blockentity;

import dev.maicra.eruruupatch.block.FilteredHopperBlock;
import dev.maicra.eruruupatch.menu.FilteredHopperMenu;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public final class FilteredHopperBlockEntity
        extends RandomizableContainerBlockEntity
        implements Hopper, WorldlyContainer {
    public static final int STORAGE_SIZE = 5;
    public static final int MOVE_ITEM_SPEED = 8;
    private static final int[] STORAGE_SLOTS = {0, 1, 2, 3, 4};
    private static final String FILTER_TAG = "Filter";

    private NonNullList<ItemStack> items = NonNullList.withSize(STORAGE_SIZE, ItemStack.EMPTY);
    private ItemStack filter = ItemStack.EMPTY;
    private int cooldownTime = -1;
    private long tickedGameTime;
    private Direction facing;
    private final Container filterContainer = new FilterContainer();
    private final IItemHandler itemHandler = new InvWrapper(this);

    public FilteredHopperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FILTERED_HOPPER.get(), pos, state);
        this.facing = state.getValue(FilteredHopperBlock.FACING);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(STORAGE_SIZE, ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
        this.cooldownTime = tag.getInt("TransferCooldown");
        this.filter = tag.contains(FILTER_TAG)
                ? ItemStack.parseOptional(registries, tag.getCompound(FILTER_TAG)).copyWithCount(1)
                : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
        tag.putInt("TransferCooldown", this.cooldownTime);
        if (!this.filter.isEmpty()) {
            tag.put(FILTER_TAG, this.filter.save(registries));
        }
    }

    @Override
    public int getContainerSize() {
        return STORAGE_SIZE;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        this.unpackLootTable(null);
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, count);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return accepts(stack);
    }

    public boolean accepts(ItemStack stack) {
        return stack.isEmpty() || this.filter.isEmpty() || stack.is(this.filter.getItem());
    }

    public ItemStack getFilter() {
        return this.filter.copy();
    }

    public void setFilter(ItemStack stack) {
        ItemStack next = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        if (ItemStack.isSameItem(this.filter, next)) return;
        this.filter = next;
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStack removeFilter() {
        ItemStack removed = this.filter;
        this.filter = ItemStack.EMPTY;
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    public Container getFilterContainer() {
        return this.filterContainer;
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.facing = state.getValue(FilteredHopperBlock.FACING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.eruruu_patch.filtered_hopper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FilteredHopperMenu(containerId, inventory, this);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return STORAGE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return accepts(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, FilteredHopperBlockEntity hopper) {
        hopper.cooldownTime--;
        hopper.tickedGameTime = level.getGameTime();
        if (!hopper.isOnCooldown()) {
            hopper.setCooldown(0);
            hopper.tryMoveItems(level, pos, state);
        }
    }

    private boolean tryMoveItems(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || isOnCooldown() || !state.getValue(FilteredHopperBlock.ENABLED)) {
            return false;
        }

        boolean moved = false;
        if (!isEmpty()) {
            moved = ejectItems(level, pos);
        }
        if (!inventoryFull()) {
            moved |= HopperBlockEntity.suckInItems(level, this);
        }

        if (moved) {
            setCooldown(MOVE_ITEM_SPEED);
            setChanged(level, pos, state);
        }
        return moved;
    }

    private boolean inventoryFull() {
        for (ItemStack stack : this.items) {
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private boolean ejectItems(Level level, BlockPos pos) {
        Container target = HopperBlockEntity.getContainerAt(level, pos.relative(this.facing));
        if (target == null) return false;
        Direction insertionSide = this.facing.getOpposite();

        for (int slot = 0; slot < STORAGE_SIZE; slot++) {
            ItemStack stored = getItem(slot);
            if (stored.isEmpty()) continue;

            int originalCount = stored.getCount();
            ItemStack remainder = HopperBlockEntity.addItem(this, target, removeItem(slot, 1), insertionSide);
            if (remainder.isEmpty()) {
                target.setChanged();
                if (target instanceof FilteredHopperBlockEntity filtered && !filtered.isOnCooldown()) {
                    int offset = filtered.tickedGameTime >= this.tickedGameTime ? 1 : 0;
                    filtered.setCooldown(MOVE_ITEM_SPEED - offset);
                }
                return true;
            }

            stored.setCount(originalCount);
            if (originalCount == 1) setItem(slot, stored);
        }
        return false;
    }

    public static void entityInside(
            Level level,
            BlockPos pos,
            BlockState state,
            Entity entity,
            FilteredHopperBlockEntity hopper) {
        if (entity instanceof ItemEntity itemEntity
                && !itemEntity.getItem().isEmpty()
                && entity.getBoundingBox()
                .move(-pos.getX(), -pos.getY(), -pos.getZ())
                .intersects(hopper.getSuckAabb())) {
            if (!hopper.isOnCooldown() && state.getValue(FilteredHopperBlock.ENABLED)
                    && HopperBlockEntity.addItem(hopper, itemEntity)) {
                hopper.setCooldown(MOVE_ITEM_SPEED);
                setChanged(level, pos, state);
            }
        }
    }

    private void setCooldown(int ticks) {
        this.cooldownTime = ticks;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    @Override
    public double getLevelX() {
        return this.worldPosition.getX() + 0.5D;
    }

    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5D;
    }

    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5D;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }

    private final class FilterContainer implements Container {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return filter.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == 0 ? filter : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            if (slot != 0 || amount <= 0) return ItemStack.EMPTY;
            return removeFilter();
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot != 0) return ItemStack.EMPTY;
            ItemStack removed = filter;
            filter = ItemStack.EMPTY;
            return removed;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot == 0) setFilter(stack);
        }

        @Override
        public void setChanged() {
            FilteredHopperBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            return FilteredHopperBlockEntity.this.stillValid(player);
        }

        @Override
        public void clearContent() {
            setFilter(ItemStack.EMPTY);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
