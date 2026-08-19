package dev.maicra.eruruupatch.menu;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import dev.maicra.eruruupatch.compat.easyfarmers.FarmerToolSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class CutterMenu extends AbstractContainerMenu {
    public static final int TOOL_SLOT = 0;
    public static final int INPUT_START = 1;
    public static final int INPUT_END = 5;
    public static final int OUTPUT_START = 5;
    public static final int OUTPUT_END = 9;
    public static final int PLAYER_START = 9;
    public static final int PLAYER_END = 45;

    private final BlockPos blockPos;
    private final ContainerData data;

    private CutterMenu(int id, Inventory inventory, BlockPos pos, CutterBlockEntity cutter) {
        this(
                id,
                inventory,
                pos,
                cutter != null ? cutter.toolHandler() : new ItemStackHandler(1),
                cutter != null ? cutter.inputHandler() : new ItemStackHandler(CutterBlockEntity.INPUT_SLOTS),
                cutter != null ? cutter.outputHandler() : new ItemStackHandler(CutterBlockEntity.OUTPUT_SLOTS),
                cutter != null ? serverData(cutter) : new SimpleContainerData(1)
        );
    }

    public CutterMenu(int id, Inventory inventory, CutterBlockEntity cutter) {
        this(id, inventory, cutter.getBlockPos(), cutter);
    }

    private CutterMenu(
            int id,
            Inventory inventory,
            BlockPos pos,
            ItemStackHandler tool,
            ItemStackHandler input,
            ItemStackHandler output,
            ContainerData data
    ) {
        super(CutterMenus.TYPE, id);
        this.blockPos = pos;
        this.data = data;
        addDataSlots(data);

        SlotItemHandler toolSlot = new SlotItemHandler(tool, 0, 142, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FarmerToolSupport.isProcessingTool(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
        addSlot(toolSlot);

        for (int i = 0; i < CutterBlockEntity.INPUT_SLOTS; i++) {
            addSlot(new SlotItemHandler(input, i, 52 + i * 18, 20));
        }
        for (int i = 0; i < CutterBlockEntity.OUTPUT_SLOTS; i++) {
            addSlot(new SlotItemHandler(output, i, 52 + i * 18, 51) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 83 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 141));
        }
    }

    /** Proper network constructor resolving the Cutter after reading its position. */
    public static CutterMenu fromNetwork(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
        CutterBlockEntity cutter = blockEntity instanceof CutterBlockEntity c ? c : null;
        return new CutterMenu(id, inventory, pos, cutter);
    }

    private static ContainerData serverData(CutterBlockEntity cutter) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return index == 0 ? cutter.progress() : 0;
            }

            @Override
            public void set(int index, int value) {
                // Client-facing progress only. Processing is authoritative server-side.
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    public int progress() {
        return data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (FarmerToolSupport.isProcessingTool(stack)) {
            // Processing tools belong only in the dedicated equipment slot. If a
            // tool is already equipped, shift-click leaves the extra tool with the
            // player instead of clogging one of the material input slots.
            if (slots.get(TOOL_SLOT).hasItem()
                    || !moveItemStackTo(stack, TOOL_SLOT, TOOL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, INPUT_START, INPUT_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.distanceToSqr(
                blockPos.getX() + 0.5D,
                blockPos.getY() + 0.5D,
                blockPos.getZ() + 0.5D
        ) > 64.0D) {
            return false;
        }
        BlockEntity entity = player.level().getBlockEntity(blockPos);
        return entity instanceof CutterBlockEntity;
    }
}
