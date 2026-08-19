package dev.maicra.eruruupatch.mixin;

import dev.maicra.eruruupatch.client.AutoMiningController;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenPauseMixin {
    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    private void eruruu$keepRunningWhileAutoMining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PauseScreen && AutoMiningController.isActive()) {
            cir.setReturnValue(false);
        }
    }
}
