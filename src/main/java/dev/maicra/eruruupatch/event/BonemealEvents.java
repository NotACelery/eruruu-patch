package dev.maicra.eruruupatch.event;

import dev.maicra.eruruupatch.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;


/** OneBlock-friendly renewable-resource additions owned by Eruruu Patch. */
public final class BonemealEvents {
    private static final float WILD_REPLACEMENT_CHANCE = 0.08F;
    private static final float MUSHROOM_CHANCE = 0.10F;
    private static final int VEGETATION_RADIUS = 4;
    private static final int HELMET_MOSS_RADIUS = 4;
    private static final int HELMET_MOSS_VERTICAL_RADIUS = 2;
    private static final List<Supplier<Block>> SURFACE_WILD_CROPS = List.of(
            farmersDelightBlock("WILD_CABBAGES"),
            farmersDelightBlock("WILD_ONIONS"),
            farmersDelightBlock("WILD_TOMATOES")
    );
    private static final Supplier<Block> WILD_RICE = farmersDelightBlock("WILD_RICE");
    private static final Queue<PendingScan> PENDING_SCANS = new ArrayDeque<>();

    private BonemealEvents() {
    }

    public static void onBonemeal(BonemealEvent event) {
        BlockState clickedState = event.getState();
        BlockPos clickedPos = event.getPos();

        // Moss Helmet interaction: Bone Meal on Stone/Cobblestone behaves like
        // bonemealing a Moss Block, without requiring the player to place Moss first.
        // Existing Moss is routed through the same explicit path while the helmet is
        // equipped so the sandbox behavior is deterministic and does not depend on
        // event ordering with BoneMealItem.
        if (isWearingMossHelmet(event.getPlayer())
                && (clickedState.is(Blocks.STONE)
                || clickedState.is(Blocks.COBBLESTONE)
                || clickedState.is(Blocks.MOSS_BLOCK))) {
            if (event.getLevel() instanceof ServerLevel level) {
                if (!clickedState.is(Blocks.MOSS_BLOCK)) {
                    level.setBlock(clickedPos, Blocks.MOSS_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                }

                Player player = event.getPlayer();
                if (player == null || !player.getAbilities().instabuild) {
                    event.getStack().shrink(1);
                }

                performVanillaMossBonemeal(level, clickedPos);
                spreadHelmetMossToStoneAndCobble(level, clickedPos);
            }
            event.setSuccessful(true);
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (clickedState.is(Blocks.GRASS_BLOCK)) {
            queueSurfaceScan(level, clickedPos);
            return;
        }

        if (clickedState.is(BlockTags.DIRT)) {
            if (tryDampDarkMushroom(level, clickedPos, event)) {
                return;
            }
            queueShallowWaterScan(level, clickedPos);
        }
    }

    public static void onServerTickPost(ServerTickEvent.Post event) {
        int pending = PENDING_SCANS.size();
        for (int i = 0; i < pending; i++) {
            PendingScan scan = PENDING_SCANS.poll();
            if (scan == null) {
                return;
            }
            if (scan.level().getServer() != event.getServer()) {
                PENDING_SCANS.add(scan);
                continue;
            }
            if (scan.type() == ScanType.SURFACE) {
                replaceFreshShortGrass(scan.level(), scan.positions());
            } else {
                replaceFreshSeagrass(scan.level(), scan.positions());
            }
        }
    }


    /**
     * Delegate the actual moss patch/vegetation generation to Minecraft's own Moss
     * block. This preserves vanilla feature placement while Eruruu only supplies the
     * helmet-specific entry point.
     */
    private static void performVanillaMossBonemeal(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.MOSS_BLOCK) && Blocks.MOSS_BLOCK instanceof BonemealableBlock moss) {
            moss.performBonemeal(level, level.getRandom(), pos, state);
        }
    }

    /**
     * Vanilla moss uses minecraft:moss_replaceable, which intentionally does not
     * include Cobblestone. NeoBlock needs both Stone and Cobblestone renewable, so
     * the helmet extends the vanilla-shaped patch with a short local propagation
     * pass. Immediate neighbours are guaranteed; subsequent rings are randomized.
     * Dirt/other vanilla replaceables are still left entirely to the vanilla feature.
     */
    private static void spreadHelmetMossToStoneAndCobble(ServerLevel level, BlockPos origin) {
        float[] chances = {1.0F, 0.55F, 0.30F};
        for (float chance : chances) {
            List<BlockPos> candidates = new ArrayList<>();
            for (int dx = -HELMET_MOSS_RADIUS; dx <= HELMET_MOSS_RADIUS; dx++) {
                for (int dy = -HELMET_MOSS_VERTICAL_RADIUS; dy <= HELMET_MOSS_VERTICAL_RADIUS; dy++) {
                    for (int dz = -HELMET_MOSS_RADIUS; dz <= HELMET_MOSS_RADIUS; dz++) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        BlockState state = level.getBlockState(pos);
                        if ((state.is(Blocks.STONE) || state.is(Blocks.COBBLESTONE))
                                && hasAdjacentMoss(level, pos)) {
                            candidates.add(pos.immutable());
                        }
                    }
                }
            }

            for (BlockPos pos : candidates) {
                BlockState state = level.getBlockState(pos);
                if (!(state.is(Blocks.STONE) || state.is(Blocks.COBBLESTONE))
                        || !hasAdjacentMoss(level, pos)
                        || (chance < 1.0F && level.getRandom().nextFloat() >= chance)) {
                    continue;
                }
                level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static boolean hasAdjacentMoss(ServerLevel level, BlockPos pos) {
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(Blocks.MOSS_BLOCK)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWearingMossHelmet(Player player) {
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MOSS_HELMET.get());
    }

    private static void queueSurfaceScan(ServerLevel level, BlockPos origin) {
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -VEGETATION_RADIUS; dx <= VEGETATION_RADIUS; dx++) {
            for (int dz = -VEGETATION_RADIUS; dz <= VEGETATION_RADIUS; dz++) {
                BlockPos ground = origin.offset(dx, 0, dz);
                BlockPos plant = ground.above();
                if (level.getBlockState(ground).is(Blocks.GRASS_BLOCK) && level.isEmptyBlock(plant)) {
                    candidates.add(plant);
                }
            }
        }
        if (!candidates.isEmpty()) {
            PENDING_SCANS.add(new PendingScan(level, ScanType.SURFACE, List.copyOf(candidates)));
        }
    }

    private static void queueShallowWaterScan(ServerLevel level, BlockPos soilPos) {
        BlockPos origin = soilPos.above();
        if (!isShallowSourceWater(level, origin)) {
            return;
        }
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -VEGETATION_RADIUS; dx <= VEGETATION_RADIUS; dx++) {
            for (int dz = -VEGETATION_RADIUS; dz <= VEGETATION_RADIUS; dz++) {
                BlockPos water = origin.offset(dx, 0, dz);
                if (isShallowSourceWater(level, water)
                        && level.getBlockState(water.below()).is(BlockTags.DIRT)) {
                    candidates.add(water);
                }
            }
        }
        if (!candidates.isEmpty()) {
            PENDING_SCANS.add(new PendingScan(level, ScanType.WATER, List.copyOf(candidates)));
        }
    }

    private static boolean isShallowSourceWater(ServerLevel level, BlockPos waterPos) {
        BlockPos above = waterPos.above();
        return level.getFluidState(waterPos).is(FluidTags.WATER)
                && level.getFluidState(waterPos).getAmount() == 8
                && level.getFluidState(above).isEmpty()
                && level.isEmptyBlock(above);
    }

    private static void replaceFreshShortGrass(ServerLevel level, List<BlockPos> positions) {
        for (BlockPos pos : positions) {
            if (!level.getBlockState(pos).is(Blocks.SHORT_GRASS)
                    || level.getRandom().nextFloat() >= WILD_REPLACEMENT_CHANCE) {
                continue;
            }
            Block crop = SURFACE_WILD_CROPS.get(level.getRandom().nextInt(SURFACE_WILD_CROPS.size())).get();
            BlockState cropState = crop.defaultBlockState();
            if (crop != Blocks.AIR && cropState.canSurvive(level, pos)) {
                level.setBlock(pos, cropState, Block.UPDATE_ALL);
                level.levelEvent(1505, pos, 8);
            }
        }
    }

    private static void replaceFreshSeagrass(ServerLevel level, List<BlockPos> positions) {
        Block wildRice = WILD_RICE.get();
        if (wildRice == Blocks.AIR) {
            return;
        }
        for (BlockPos waterPos : positions) {
            if (!level.getBlockState(waterPos).is(Blocks.SEAGRASS)
                    || !isShallowSourceWaterAfterSeagrass(level, waterPos)
                    || level.getRandom().nextFloat() >= WILD_REPLACEMENT_CHANCE) {
                continue;
            }
            BlockState lower = wildRice.defaultBlockState();
            if (lower.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                lower = lower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
            }
            if (lower.hasProperty(BlockStateProperties.WATERLOGGED)) {
                lower = lower.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            BlockState upper = wildRice.defaultBlockState();
            if (upper.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                upper = upper.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
            }
            if (upper.hasProperty(BlockStateProperties.WATERLOGGED)) {
                upper = upper.setValue(BlockStateProperties.WATERLOGGED, false);
            }
            level.setBlock(waterPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
            if (lower.canSurvive(level, waterPos)) {
                level.setBlock(waterPos, lower, Block.UPDATE_ALL);
                level.setBlock(waterPos.above(), upper, Block.UPDATE_ALL);
                level.levelEvent(1505, waterPos, 8);
            }
        }
    }

    private static boolean isShallowSourceWaterAfterSeagrass(ServerLevel level, BlockPos waterPos) {
        return level.getBlockState(waterPos.below()).is(BlockTags.DIRT)
                && level.getFluidState(waterPos).is(FluidTags.WATER)
                && level.getFluidState(waterPos).getAmount() == 8
                && level.getFluidState(waterPos.above()).isEmpty()
                && level.isEmptyBlock(waterPos.above());
    }

    /**
     * A Dirt floor with two blocks of interior air, a solid ceiling, source
     * water above that ceiling, and local light <= 7. Every valid attempt
     * consumes Bone Meal; 10% succeeds, split 50/50 brown/red.
     */
    private static boolean tryDampDarkMushroom(ServerLevel level, BlockPos soilPos, BonemealEvent event) {
        if (!level.getBlockState(soilPos).is(Blocks.DIRT)) {
            return false;
        }

        BlockPos mushroomPos = soilPos.above();
        BlockPos headPos = soilPos.above(2);
        BlockPos ceilingPos = soilPos.above(3);
        BlockPos waterPos = soilPos.above(4);

        if (!level.isEmptyBlock(mushroomPos)
                || !level.isEmptyBlock(headPos)
                || !level.getBlockState(ceilingPos).isCollisionShapeFullBlock(level, ceilingPos)
                || !level.getFluidState(waterPos).is(FluidTags.WATER)
                || level.getFluidState(waterPos).getAmount() != 8
                || level.getMaxLocalRawBrightness(mushroomPos) > 7) {
            return false;
        }

        Player player = event.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            event.getStack().shrink(1);
        }

        // The guaranteed Bone Meal feedback belongs on the clicked soil block.
        // mushroomPos is AIR on failed rolls, which makes the vanilla 1505 effect invisible.
        level.levelEvent(1505, soilPos, 15);

        if (level.getRandom().nextFloat() < MUSHROOM_CHANCE) {
            Block mushroom = level.getRandom().nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM;
            BlockState state = mushroom.defaultBlockState();
            if (state.canSurvive(level, mushroomPos)) {
                level.setBlock(mushroomPos, state, Block.UPDATE_ALL);
                level.levelEvent(1505, mushroomPos, 15);
            }
        }

        event.setSuccessful(true);
        return true;
    }

    private static Supplier<Block> farmersDelightBlock(String fieldName) {
        try {
            Class<?> modBlocks = Class.forName("vectorwing.farmersdelight.common.registry.ModBlocks");
            Object fieldValue = modBlocks.getField(fieldName).get(null);
            if (fieldValue instanceof Supplier<?> supplier) {
                return () -> {
                    Object block = supplier.get();
                    return block instanceof Block resolved ? resolved : Blocks.AIR;
                };
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // The required dependency should make this unreachable during normal play.
        }
        return () -> Blocks.AIR;
    }

    private enum ScanType { SURFACE, WATER }

    private record PendingScan(ServerLevel level, ScanType type, List<BlockPos> positions) {
    }

}
