package dev.maicra.eruruupatch.mixin;

import dev.maicra.eruruupatch.client.AutoMiningController;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftAutoMiningMixin {
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true, remap = false)
    private void eruruu$autoMining(boolean leftClick, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!AutoMiningController.isActive()) {
            return;
        }
        AutoMiningController.continueMining(minecraft);
        ci.cancel();
    }
}
