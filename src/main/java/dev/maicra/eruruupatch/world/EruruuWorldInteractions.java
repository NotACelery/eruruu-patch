package dev.maicra.eruruupatch.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = "eruruu_patch")
public final class EruruuWorldInteractions {
    private static final ResourceLocation MOSS_HELMET =
            ResourceLocation.fromNamespaceAndPath("eruruu_patch", "moss_helmet");
    private static final float SWEET_BERRY_CHANCE = 0.03F;
    private static final List<PendingBerryScan> BERRY_SCANS = new ArrayList<>();

    private EruruuWorldInteractions() {
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onBonemeal(BonemealEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (event.getState().is(Blocks.GRASS_BLOCK)) {
            List<BlockPos> positions = new ArrayList<>();
            BlockPos origin = event.getPos();
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos ground = origin.offset(dx, 0, dz);
                    BlockPos plant = ground.above();
                    if (level.getBlockState(ground).is(Blocks.GRASS_BLOCK)
                            && level.getBlockState(plant).isAir()) {
                        positions.add(plant.immutable());
                    }
                }
            }
            if (!positions.isEmpty()) {
                BERRY_SCANS.add(new PendingBerryScan(level, positions, level.getGameTime() + 1L));
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof HoeItem) || !isWearingMossHelmet(player)) {
            return;
        }
        if (!event.getLevel().getBlockState(event.getPos()).is(Blocks.MOSS_BLOCK)) {
            return;
        }

        if (!event.getLevel().isClientSide) {
            event.getLevel().setBlockAndUpdate(event.getPos(), Blocks.DIRT.defaultBlockState());
            held.hurtAndBreak(
                    1,
                    player,
                    event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
            );
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Iterator<PendingBerryScan> berryIterator = BERRY_SCANS.iterator();
        while (berryIterator.hasNext()) {
            PendingBerryScan pending = berryIterator.next();
            if (pending.level.getGameTime() < pending.due) {
                continue;
            }
            berryIterator.remove();
            for (BlockPos pos : pending.positions) {
                if (pending.level.getBlockState(pos).is(Blocks.SHORT_GRASS)
                        && pending.level.getRandom().nextFloat() < SWEET_BERRY_CHANCE) {
                    pending.level.setBlockAndUpdate(pos, Blocks.SWEET_BERRY_BUSH.defaultBlockState());
                }
            }
        }
    }

    private static boolean isWearingMossHelmet(Player player) {
        if (player == null) {
            return false;
        }
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return !head.isEmpty() && MOSS_HELMET.equals(BuiltInRegistries.ITEM.getKey(head.getItem()));
    }

    private record PendingBerryScan(ServerLevel level, List<BlockPos> positions, long due) {
    }
}
