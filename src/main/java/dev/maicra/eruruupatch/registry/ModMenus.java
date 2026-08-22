package dev.maicra.eruruupatch.registry;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.menu.FilteredHopperMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = EruruuPatch.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModMenus {
    public static MenuType<FilteredHopperMenu> FILTERED_HOPPER;

    private ModMenus() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) return;
        FILTERED_HOPPER = IMenuTypeExtension.create(FilteredHopperMenu::fromNetwork);
        event.register(
                Registries.MENU,
                ResourceLocation.fromNamespaceAndPath(EruruuPatch.MOD_ID, "filtered_hopper"),
                () -> FILTERED_HOPPER
        );
    }
}
