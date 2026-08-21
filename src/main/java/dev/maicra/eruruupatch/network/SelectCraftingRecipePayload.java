package dev.maicra.eruruupatch.network;

import dev.maicra.eruruupatch.EruruuPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectCraftingRecipePayload(
        int containerId,
        long inputSignature,
        ResourceLocation recipeId
) implements CustomPacketPayload {
    public static final Type<SelectCraftingRecipePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "select_crafting_recipe")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectCraftingRecipePayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeVarInt(payload.containerId());
                buffer.writeLong(payload.inputSignature());
                buffer.writeResourceLocation(payload.recipeId());
            },
            buffer -> new SelectCraftingRecipePayload(
                    buffer.readVarInt(),
                    buffer.readLong(),
                    buffer.readResourceLocation()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
