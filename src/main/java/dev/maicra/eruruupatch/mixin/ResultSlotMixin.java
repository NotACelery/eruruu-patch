package dev.maicra.eruruupatch.mixin;

import dev.maicra.eruruupatch.crafting.CraftingConflictResolver;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {
    @Shadow @Final private Player player;

    @Redirect(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRemainingItemsFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;"
            )
    )
    @SuppressWarnings({"rawtypes", "unchecked"})
    private NonNullList<ItemStack> eruruu$useSelectedRecipeRemainders(
            RecipeManager manager,
            RecipeType type,
            RecipeInput input,
            Level level
    ) {
        if (this.player instanceof ServerPlayer serverPlayer && input instanceof CraftingInput craftingInput) {
            NonNullList<ItemStack> selected = CraftingConflictResolver.selectedRemainders(
                    serverPlayer,
                    craftingInput,
                    level
            );
            if (selected != null) {
                return selected;
            }
        }

        return manager.getRemainingItemsFor(type, input, level);
    }
}
