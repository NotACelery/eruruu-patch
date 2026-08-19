package dev.maicra.eruruupatch.registry;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.block.CutterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EruruuPatch.MOD_ID);

    public static final DeferredBlock<CutterBlock> CUTTER = BLOCKS.registerBlock(
            "cutter",
            CutterBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
    );

    private ModBlocks() {
    }
}
