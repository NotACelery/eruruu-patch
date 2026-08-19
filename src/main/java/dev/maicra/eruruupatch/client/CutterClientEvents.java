package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.menu.CutterMenus;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "eruruu_patch", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class CutterClientEvents {
    private CutterClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CutterMenus.TYPE, CutterScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CUTTER.get(), CutterBlockEntityRenderer::new);
    }
}
