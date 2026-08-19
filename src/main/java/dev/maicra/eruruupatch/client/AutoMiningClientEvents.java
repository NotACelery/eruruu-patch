package dev.maicra.eruruupatch.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "eruruu_patch", value = Dist.CLIENT)
public final class AutoMiningClientEvents {
    private AutoMiningClientEvents() {
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != 0 || event.getAction() != 1) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        // Clicks inside chat/inventory/JEI/backpacks/etc. are UI clicks, not the
        // in-world toggle, and must never switch Auto Mining off.
        if (minecraft.screen != null) {
            return;
        }

        if (AutoMiningController.isActive()) {
            AutoMiningController.deactivate(minecraft);
            event.setCanceled(true);
            return;
        }

        // Do not cancel the activation click: vanilla begins the first break and the
        // controller takes over subsequent continueAttack calls.
        if (AutoMiningController.canActivate(minecraft)) {
            AutoMiningController.activate(minecraft);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        AutoMiningController.validateState(minecraft);
        if (AutoMiningController.isActive() && minecraft.screen != null) {
            AutoMiningController.continueMining(minecraft);
        }
    }

    @SubscribeEvent
    public static void onHud(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            renderIndicator(event.getGuiGraphics());
        }
    }

    private static void renderIndicator(GuiGraphics graphics) {
        if (!AutoMiningController.isActive()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        AutoMiningController.validateState(minecraft);
        if (!AutoMiningController.isActive()) {
            return;
        }

        Component text = AutoMiningController.indicatorText();
        int textWidth = minecraft.font.width(text);
        int width = textWidth + 16;
        int height = 20;
        int x = (graphics.guiWidth() - width) / 2;
        int y = graphics.guiHeight() - 64;

        graphics.fill(x, y, x + width, y + height, -1341124592);
        graphics.renderOutline(x, y, width, height, -7624093);
        graphics.drawString(minecraft.font, text, x + 8, y + 6, -1, true);
    }
}
