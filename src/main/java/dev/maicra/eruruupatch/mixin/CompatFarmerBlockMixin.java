package dev.maicra.eruruupatch.mixin;

import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.maicra.eruruupatch.menu.PaddyFarmerMenu;
import dev.maicra.eruruupatch.menu.RichFarmerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CompatFarmerBlock.class, remap = false)
public abstract class CompatFarmerBlockMixin {
    @Inject(method = "openOutput", at = @At("HEAD"), cancellable = true, remap = false)
    private void eruruu$openRichOutput(Level level, BlockPos pos, Player player,
                                       CompatFarmerBlockEntity farmer, BlockState state,
                                       CallbackInfo ci) {
        boolean rich = farmer.variant().isRich();
        boolean paddy = farmer.variant().isAquatic() && !rich;
        if (!rich && !paddy) {
            return;
        }

        ci.cancel();
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (id, inventory, menuPlayer) -> rich
                                ? new RichFarmerMenu(id, inventory, farmer)
                                : new PaddyFarmerMenu(id, inventory, farmer),
                        Component.translatable(state.getBlock().getDescriptionId())
                ),
                buffer -> buffer.writeBlockPos(pos)
        );
    }
    /**
     * Pick Block must never clone the stored villager/crop/rope/tool data. Normal
     * block breaking still uses CompatFarmerBlock#getDrops and therefore keeps
     * the machine contents exactly as before. This only sanitizes creative clone.
     */
    @Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true, remap = false)
    private void eruruu$cleanCreativeClone(BlockState state, HitResult target, LevelReader level, BlockPos pos,
                                            Player player, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(new ItemStack(state.getBlock()));
    }

}
