package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.event.ReinforcedPickaxeEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;

public final class AutoMiningController {
    private static final double MOVEMENT_EPSILON_SQ = 1.0E-6D;

    private static boolean active;
    private static Vec3 anchor;
    private static Object anchorLevel;

    private AutoMiningController() {
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean canActivate(Minecraft minecraft) {
        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.gameMode == null
                || minecraft.screen != null) {
            return false;
        }
        if (!ReinforcedPickaxeEvents.isReinforced(minecraft.player.getMainHandItem())) {
            return false;
        }

        HitResult hit = minecraft.player.pick(minecraft.player.blockInteractionRange(), 1.0F, false);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return false;
        }
        return !minecraft.level.getBlockState(blockHit.getBlockPos()).isAir();
    }

    public static void activate(Minecraft minecraft) {
        if (!canActivate(minecraft)) {
            return;
        }
        active = true;
        anchor = minecraft.player.position();
        anchorLevel = minecraft.level;
    }

    public static void deactivate(Minecraft minecraft) {
        if (!active) {
            return;
        }
        active = false;
        anchor = null;
        anchorLevel = null;
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.stopDestroyBlock();
        }
    }

    public static void validateState(Minecraft minecraft) {
        if (!active) {
            return;
        }
        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.gameMode == null
                || !minecraft.player.isAlive()
                || anchorLevel != minecraft.level
                || !ReinforcedPickaxeEvents.isReinforced(minecraft.player.getMainHandItem())) {
            deactivate(minecraft);
            return;
        }

        if (anchor == null || minecraft.player.position().distanceToSqr(anchor) > MOVEMENT_EPSILON_SQ) {
            deactivate(minecraft);
        }
    }

    public static void continueMining(Minecraft minecraft) {
        validateState(minecraft);
        if (!active || minecraft.player == null || minecraft.level == null || minecraft.gameMode == null) {
            return;
        }

        if (minecraft.player.isUsingItem()) {
            minecraft.gameMode.stopDestroyBlock();
            return;
        }

        HitResult hit = minecraft.player.pick(minecraft.player.blockInteractionRange(), 1.0F, false);
        if (hit instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            if (!minecraft.level.getBlockState(pos).isAir()) {
                var click = ClientHooks.onClickInput(0, minecraft.options.keyAttack, InteractionHand.MAIN_HAND);
                if (click.isCanceled()) {
                    if (click.shouldSwingHand()) {
                        minecraft.particleEngine.addBlockHitEffects(pos, blockHit);
                        minecraft.player.swing(InteractionHand.MAIN_HAND);
                    }
                    return;
                }

                if (minecraft.gameMode.continueDestroyBlock(pos, blockHit.getDirection()) && click.shouldSwingHand()) {
                    minecraft.particleEngine.addBlockHitEffects(pos, blockHit);
                    minecraft.player.swing(InteractionHand.MAIN_HAND);
                }
                return;
            }
        }

        minecraft.gameMode.stopDestroyBlock();
    }

    public static Component indicatorText() {
        return Component.translatable("gui.eruruu_patch.auto_mining");
    }
}
