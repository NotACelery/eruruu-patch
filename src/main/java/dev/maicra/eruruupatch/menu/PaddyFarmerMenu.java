package dev.maicra.eruruupatch.menu;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
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

/** Paddy output menu with the same block-title presentation used by Rich Farmers. */
public final class PaddyFarmerMenu extends AbstractContainerMenu {
    private static final int OUTPUT_SLOTS = 4;
    private static final int PLAYER_START = 4;
    private static final int PLAYER_END = 40;

    private final BlockPos blockPos;
    private final Player menuPlayer;

    public PaddyFarmerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, buffer.readBlockPos(), new SimpleContainer(OUTPUT_SLOTS));
    }

    public PaddyFarmerMenu(int id, Inventory inventory, CompatFarmerBlockEntity farmer) {
        this(
                id,
                inventory,
                farmer.getBlockPos(),
                farmer.easyVillagers().getOutputInventory(inventory.player.level().registryAccess())
        );
    }

    private PaddyFarmerMenu(int id, Inventory inventory, BlockPos blockPos, Container output) {
        super(RichFarmerMenus.PADDY_TYPE, id);
        this.blockPos = blockPos;
        this.menuPlayer = inventory.player;

        Container safeOutput = output != null && output.getContainerSize() >= OUTPUT_SLOTS
                ? output
                : new SimpleContainer(OUTPUT_SLOTS);

        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            addSlot(new Slot(safeOutput, i, 52 + i * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 51 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 109));
        }
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
        } else {
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
        if (player.level() != menuPlayer.level()) {
            return false;
        }
        if (player.distanceToSqr(
                blockPos.getX() + 0.5D,
                blockPos.getY() + 0.5D,
                blockPos.getZ() + 0.5D
        ) > 64.0D) {
            return false;
        }

        BlockEntity entity = player.level().getBlockEntity(blockPos);
        return entity instanceof CompatFarmerBlockEntity farmer
                && farmer.variant().isAquatic()
                && !farmer.variant().isRich();
    }
}
