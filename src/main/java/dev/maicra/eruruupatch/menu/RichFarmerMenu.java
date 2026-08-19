package dev.maicra.eruruupatch.menu;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.maicra.eruruupatch.compat.easyfarmers.KnifeHolder;
import dev.maicra.eruruupatch.compat.easyfarmers.FarmerToolSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Easy Villagers output-style menu plus a persistent Knife equipment slot. */
public final class RichFarmerMenu extends AbstractContainerMenu {
    public static final int OUTPUT_SLOTS = 4;
    public static final int KNIFE_SLOT = 4;
    private static final int PLAYER_START = 5;
    private static final int PLAYER_END = 41;

    private final BlockPos blockPos;
    private final Player menuPlayer;
    private final KnifeContainer knifeContainer;

    public RichFarmerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, buffer.readBlockPos(), new SimpleContainer(OUTPUT_SLOTS), new KnifeContainer(null));
    }

    public RichFarmerMenu(int id, Inventory inventory, CompatFarmerBlockEntity farmer) {
        this(
                id,
                inventory,
                farmer.getBlockPos(),
                farmer.easyVillagers().getOutputInventory(inventory.player.level().registryAccess()),
                new KnifeContainer((KnifeHolder) (Object) farmer)
        );
    }

    private RichFarmerMenu(
            int id,
            Inventory inventory,
            BlockPos blockPos,
            Container output,
            KnifeContainer knifeContainer
    ) {
        super(RichFarmerMenus.TYPE, id);
        this.blockPos = blockPos;
        this.menuPlayer = inventory.player;
        this.knifeContainer = knifeContainer;

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

        Slot knifeSlot = new Slot(knifeContainer, 0, 142, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FarmerToolSupport.isKnife(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
        knifeSlot.setBackground(InventoryMenu.BLOCK_ATLAS, FarmerToolSupport.EMPTY_KNIFE_SLOT);
        addSlot(knifeSlot);

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
        ItemStack empty = ItemStack.EMPTY;
        if (index < 0 || index >= slots.size()) {
            return empty;
        }

        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                return empty;
            }
        } else if (FarmerToolSupport.isKnife(stack) && knifeContainer.getItem(0).isEmpty()) {
            // One deterministic equipment transfer. This avoids relying on the
            // generic merge path for our one-slot SimpleContainer and guarantees
            // the KnifeHolder receives setChanged() on the server.
            knifeContainer.setItem(0, stack.copyWithCount(1));
            stack.shrink(1);
        } else {
            return empty;
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
        return entity instanceof CompatFarmerBlockEntity farmer && farmer.variant().isRich();
    }

    private static final class KnifeContainer extends SimpleContainer {
        private final KnifeHolder holder;
        private boolean initializing;

        private KnifeContainer(KnifeHolder holder) {
            super(1);
            this.holder = holder;
            if (holder != null) {
                initializing = true;
                super.setItem(0, holder.eruruu$getKnife());
                initializing = false;
            }
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (!initializing && holder != null) {
                holder.eruruu$setKnife(getItem(0));
            }
        }
    }
}
