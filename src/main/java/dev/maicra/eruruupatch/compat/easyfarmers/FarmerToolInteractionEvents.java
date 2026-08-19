package dev.maicra.eruruupatch.compat.easyfarmers;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Direct world-equipment interactions shared by the Rich Farmer family and the
 * Cutter. This lives on NeoForge's RightClickBlock event instead of relying on
 * the target block's internal useItemOn ordering, so Knife/Axe insertion wins
 * consistently before the normal GUI-open path.
 */
@EventBusSubscriber(modid = "eruruu_patch")
public final class FarmerToolInteractionEvents {
    private FarmerToolInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player == null || player.isShiftKeyDown()) {
            return;
        }

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        boolean equipped = false;

        if (blockEntity instanceof CutterBlockEntity cutter) {
            equipped = tryEquipCutter(cutter, held, player, event.getLevel().isClientSide);
        } else if (blockEntity instanceof CompatFarmerBlockEntity farmer
                && farmer.variant().isRich()) {
            equipped = tryEquipRichFarmer(farmer, held, player, event.getLevel().isClientSide);
        }

        if (!equipped) {
            // Slot already occupied, invalid tool, normal Paddy, etc.: let the
            // target block continue normally so its GUI opens as expected.
            return;
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }

    private static boolean tryEquipRichFarmer(CompatFarmerBlockEntity farmer, ItemStack held, Player player, boolean clientSide) {
        if (!FarmerToolSupport.isKnife(held)) {
            return false;
        }

        KnifeHolder holder = (KnifeHolder) (Object) farmer;
        if (!holder.eruruu$getKnife().isEmpty()) {
            return false;
        }

        if (!clientSide) {
            holder.eruruu$setKnife(held.copyWithCount(1));
            consumeOne(held, player);
        }
        return true;
    }

    private static boolean tryEquipCutter(CutterBlockEntity cutter, ItemStack held, Player player, boolean clientSide) {
        if (!FarmerToolSupport.isProcessingTool(held) || !cutter.toolHandler().getStackInSlot(0).isEmpty()) {
            return false;
        }

        if (!clientSide) {
            ItemStack one = held.copyWithCount(1);
            ItemStack remainder = cutter.toolHandler().insertItem(0, one, false);
            if (!remainder.isEmpty()) {
                return false;
            }
            consumeOne(held, player);
        }
        return true;
    }

    private static void consumeOne(ItemStack stack, Player player) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }
}
