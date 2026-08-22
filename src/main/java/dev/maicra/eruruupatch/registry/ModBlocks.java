package dev.maicra.eruruupatch.registry;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.block.FilteredHopperBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EruruuPatch.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EruruuPatch.MOD_ID);

    public static final DeferredBlock<FilteredHopperBlock> FILTERED_HOPPER = BLOCKS.registerBlock(
            "filtered_hopper",
            FilteredHopperBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER)
    );

    public static final DeferredItem<BlockItem> FILTERED_HOPPER_ITEM = ITEMS.register(
            "filtered_hopper",
            () -> new BlockItem(FILTERED_HOPPER.get(), new Item.Properties())
    );

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
