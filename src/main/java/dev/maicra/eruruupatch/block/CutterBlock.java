package dev.maicra.eruruupatch.block;

import dev.maicra.eruruupatch.ModItems;
import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import dev.maicra.eruruupatch.menu.CutterMenu;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/** Easy-Villagers-style enclosure for automated Farmer's Delight cutting. */
public final class CutterBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CutterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CutterBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.CUTTER.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof net.minecraft.server.level.ServerLevel serverLevel
                    && blockEntity instanceof CutterBlockEntity cutter) {
                CutterBlockEntity.serverTick(serverLevel, tickPos, tickState, cutter);
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack heldItem,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!(level.getBlockEntity(pos) instanceof CutterBlockEntity cutter)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Match Easy Villagers: sneaking removes the stored villager even when the
        // player happens to be holding another item. The previous implementation
        // only handled the empty-hand useWithoutItem path, effectively trapping the
        // villager for normal shift-right-click usage.
        if (player.isShiftKeyDown() && cutter.hasVillager()) {
            if (!level.isClientSide) {
                ItemStack removed = cutter.removeVillager();
                if (!removed.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, removed);
                }
                level.playSound(null, pos, SoundEvents.VILLAGER_CELEBRATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!cutter.hasVillager() && cutter.isVillagerItem(heldItem)) {
            if (!level.isClientSide && cutter.insertVillager(heldItem)) {
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.VILLAGER_YES, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        openMenu(level, pos, player, cutter);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CutterBlockEntity cutter)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && cutter.hasVillager()) {
            if (!level.isClientSide) {
                ItemStack removed = cutter.removeVillager();
                if (!removed.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, removed);
                }
                level.playSound(null, pos, SoundEvents.VILLAGER_CELEBRATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        openMenu(level, pos, player, cutter);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void openMenu(Level level, BlockPos pos, Player player, CutterBlockEntity cutter) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        serverPlayer.openMenu(
                new net.minecraft.world.SimpleMenuProvider(
                        (id, inventory, ignored) -> new CutterMenu(id, inventory, cutter),
                        Component.translatable("container.eruruu_patch.cutter")
                ),
                buffer -> buffer.writeBlockPos(pos)
        );
    }


    /**
     * Creative Pick Block keeps only the Cutter material variant, never machine contents.
     * This mirrors the clone-safety rule used by the Farmer blocks while preventing every
     * non-Oak Cutter from collapsing back to the zero-data Oak variant.
     */
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CutterBlockEntity cutter) {
            return CutterLogVariant.createCutter(cutter.logVariant());
        }
        return new ItemStack(ModItems.CUTTER.get());
    }

    /** Keep Villager, tool, inputs and outputs inside the dropped Cutter item. */
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack dropped = new ItemStack(ModItems.CUTTER.get());
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CutterBlockEntity cutter) {
            CompoundTag data = cutter.saveCustomOnly(params.getLevel().registryAccess());
            if (!data.isEmpty()) {
                BlockItem.setBlockEntityData(dropped, ModBlockEntities.CUTTER.get(), data);
            }
            // Empty machines stack by log variant. Any machine carrying actual
            // Villager/tool/input/output/progress data is forced to a single item,
            // preventing populated Cutters from ever merging into a stack.
            if (cutter.hasStoredContents()) {
                dropped.set(DataComponents.MAX_STACK_SIZE, 1);
            }
        }
        return List.of(dropped);
    }
}
