package dev.maicra.eruruupatch.registry;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.blockentity.FilteredHopperBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            EruruuPatch.MOD_ID
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FilteredHopperBlockEntity>> FILTERED_HOPPER =
            BLOCK_ENTITIES.register(
                    "filtered_hopper",
                    () -> BlockEntityType.Builder.of(
                            FilteredHopperBlockEntity::new,
                            ModBlocks.FILTERED_HOPPER.get()
                    ).build(null)
            );

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FILTERED_HOPPER.get(),
                (hopper, side) -> hopper.getItemHandler()
        );
    }

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
