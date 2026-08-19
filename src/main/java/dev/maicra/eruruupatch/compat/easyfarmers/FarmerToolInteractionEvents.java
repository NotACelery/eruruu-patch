package dev.maicra.eruruupatch.compat.easyfarmers;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Legacy interaction support for Cutters already created by the Eruruu sandbox.
 * Native Easy Farmer's Delight Compat 1.1.0 owns Farmer Knife interaction itself.
 */
@EventBusSubscriber(modid = "eruruu_patch")
public final class FarmerToolInteractionEvents {
    private FarmerToolInteractionEvents() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player == null || player.isShiftKeyDown()) return;

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (!(blockEntity instanceof CutterBlockEntity cutter)) return;
        if (!tryEquipLegacyCutter(cutter, held, player, event.getLevel().isClientSide)) return;

        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }

    private static boolean tryEquipLegacyCutter(CutterBlockEntity cutter, ItemStack held, Player player, boolean clientSide) {
        if (!FarmerToolSupport.isProcessingTool(held) || !cutter.toolHandler().getStackInSlot(0).isEmpty()) return false;
        if (!clientSide) {
            ItemStack remainder = cutter.toolHandler().insertItem(0, held.copyWithCount(1), false);
            if (!remainder.isEmpty()) return false;
            if (!player.getAbilities().instabuild) held.shrink(1);
        }
        return true;
    }
}
