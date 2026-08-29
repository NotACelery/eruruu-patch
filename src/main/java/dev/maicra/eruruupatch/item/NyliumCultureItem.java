package dev.maicra.eruruupatch.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class NyliumCultureItem extends Item {
    private final Block targetNylium;

    public NyliumCultureItem(Properties properties, Block targetNylium) {
        super(properties);
        this.targetNylium = targetNylium;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!level.getBlockState(pos).is(Blocks.NETHERRACK)) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (!level.isClientSide()) {
            level.setBlock(pos, targetNylium.defaultBlockState(), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.NYLIUM_PLACE, SoundSource.BLOCKS, 0.9F, 1.0F);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5D,
                        pos.getY() + 1.0D,
                        pos.getZ() + 0.5D,
                        8,
                        0.25D,
                        0.15D,
                        0.25D,
                        0.0D
                );
            }

            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
