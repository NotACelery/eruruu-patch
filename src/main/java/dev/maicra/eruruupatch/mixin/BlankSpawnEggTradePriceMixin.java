package dev.maicra.eruruupatch.mixin;

import dev.maicra.eruruupatch.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps the Farmer recovery trade at a two-emerald floor.
 *
 * <p>A cured villager contributes enough positive reputation for the custom
 * 0.08 price multiplier to reduce the clean 10-emerald base price to 2. The
 * normal trading gossip earned while levelling that same Farmer can otherwise
 * push the offer down to vanilla's global 1-emerald floor. We intentionally
 * keep this one recovery trade at 2 instead, matching its progression design.</p>
 */
@Mixin(MerchantOffer.class)
public abstract class BlankSpawnEggTradePriceMixin {
    @Shadow @Final private ItemStack result;

    @Inject(method = "getModifiedCostCount", at = @At("RETURN"), cancellable = true)
    private void eruruu$keepBlankSpawnEggTradeAtTwoEmeralds(
            ItemCost cost,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (cir.getReturnValue() >= 2
                || !result.is(ModItems.BLANK_SPAWN_EGG.get())
                || !cost.itemStack().is(Items.EMERALD)) {
            return;
        }

        cir.setReturnValue(2);
    }
}
