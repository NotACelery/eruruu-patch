package dev.maicra.eruruupatch.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "eruruu_patch", bus = EventBusSubscriber.Bus.MOD)
public final class CutterMenus {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("eruruu_patch", "cutter");
    public static MenuType<CutterMenu> TYPE;

    private CutterMenus() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) {
            return;
        }
        TYPE = IMenuTypeExtension.create(CutterMenu::fromNetwork);
        event.register(Registries.MENU, ID, () -> TYPE);
    }
}
