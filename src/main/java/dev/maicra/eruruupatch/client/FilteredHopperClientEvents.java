package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = EruruuPatch.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FilteredHopperClientEvents {
    private FilteredHopperClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.FILTERED_HOPPER, FilteredHopperScreen::new);
    }
}
