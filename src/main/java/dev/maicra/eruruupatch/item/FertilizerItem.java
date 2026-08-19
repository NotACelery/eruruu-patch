package dev.maicra.eruruupatch.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class FertilizerItem extends Item {
    public FertilizerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.getBlockState(context.getClickedPos()).is(Blocks.DIRT)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(context.getClickedPos(), Blocks.GRASS_BLOCK.defaultBlockState());
            level.playSound(null, context.getClickedPos(), SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        context.getClickedPos().getX() + 0.5,
                        context.getClickedPos().getY() + 1.0,
                        context.getClickedPos().getZ() + 0.5,
                        8,
                        0.35,
                        0.15,
                        0.35,
                        0.0
                );
            }

            Player player = context.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
