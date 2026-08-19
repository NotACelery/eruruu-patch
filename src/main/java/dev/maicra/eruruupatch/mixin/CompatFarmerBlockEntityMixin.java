package dev.maicra.eruruupatch.mixin;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.integration.EasyVillagersFarmerAdapter;
import dev.maicra.eruruupatch.compat.easyfarmers.KnifeHolder;
import dev.maicra.eruruupatch.compat.easyfarmers.FarmerToolSupport;
import dev.maicra.eruruupatch.compat.easyfarmers.HarvestResolver;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CompatFarmerBlockEntity.class, remap = false)
public abstract class CompatFarmerBlockEntityMixin implements KnifeHolder {
    @Unique
    private static final String ERURUU_KNIFE_KEY = "EruruuKnife";

    @Unique
    private ItemStack eruruu$knife = ItemStack.EMPTY;

    @Shadow
    @Final
    private EasyVillagersFarmerAdapter easyVillagers;

    @Override
    public ItemStack eruruu$getKnife() {
        return eruruu$knife.copy();
    }

    @Override
    public void eruruu$setKnife(ItemStack stack) {
        ItemStack normalized = FarmerToolSupport.normalizeKnife(stack);
        eruruu$knife = normalized;
        ((CompatFarmerBlockEntity) (Object) this).setChanged();
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"), remap = false)
    private void eruruu$loadKnife(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (tag.contains(ERURUU_KNIFE_KEY)) {
            ItemStack loaded = ItemStack.parseOptional(registries, tag.getCompound(ERURUU_KNIFE_KEY));
            eruruu$knife = FarmerToolSupport.normalizeKnife(loaded);
        } else {
            eruruu$knife = ItemStack.EMPTY;
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = false)
    private void eruruu$saveKnife(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (!eruruu$knife.isEmpty()) {
            Tag saved = eruruu$knife.save(registries);
            tag.put(ERURUU_KNIFE_KEY, saved);
        } else {
            tag.remove(ERURUU_KNIFE_KEY);
        }
    }

    @ModifyArg(
            method = "ageNormalCropSafely",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withParameter(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParam;Ljava/lang/Object;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;",
                    ordinal = 2,
                    remap = false
            ),
            index = 1,
            remap = false
    )
    private Object eruruu$normalCropKnife(Object original) {
        return eruruu$lootTool(original);
    }

    @ModifyArg(
            method = "harvestMatureRice",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withParameter(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParam;Ljava/lang/Object;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;",
                    ordinal = 2,
                    remap = false
            ),
            index = 1,
            remap = false
    )
    private Object eruruu$riceKnife(Object original) {
        return eruruu$lootTool(original);
    }

    @Inject(method = "ageMushroomColony", at = @At("HEAD"), cancellable = true, remap = false)
    private void eruruu$requireKnifeForMatureMushrooms(ServerLevel level, HolderLookup.Provider registries,
                                                       CallbackInfoReturnable<Boolean> cir) {
        CompatFarmerBlockEntity farmer = (CompatFarmerBlockEntity) (Object) this;
        BlockState crop = easyVillagers.getCrop(registries);
        if (HarvestResolver.shouldWaitForKnife(farmer.variant().isRich(), crop, eruruu$knife)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private Object eruruu$lootTool(Object original) {
        CompatFarmerBlockEntity farmer = (CompatFarmerBlockEntity) (Object) this;
        ItemStack fallback = original instanceof ItemStack stack ? stack : ItemStack.EMPTY;
        return HarvestResolver.lootTool(farmer.variant().isRich(), eruruu$knife, fallback);
    }

}
