package dev.maicra.eruruupatch.network;

import dev.maicra.eruruupatch.crafting.CraftingConflictResolver;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(
                        SelectCraftingRecipePayload.TYPE,
                        SelectCraftingRecipePayload.STREAM_CODEC,
                        ModNetworking::handleSelectCraftingRecipe
                );
    }

    private static void handleSelectCraftingRecipe(
            SelectCraftingRecipePayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            CraftingConflictResolver.handleSelection(serverPlayer, payload);
        }
    }
}
