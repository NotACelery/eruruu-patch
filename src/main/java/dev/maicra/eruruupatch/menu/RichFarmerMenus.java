package dev.maicra.eruruupatch.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "eruruu_patch", bus = EventBusSubscriber.Bus.MOD)
public final class RichFarmerMenus {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("eruruu_patch", "rich_farmer_output");
    public static final ResourceLocation PADDY_ID = ResourceLocation.fromNamespaceAndPath("eruruu_patch", "paddy_farmer_output");
    public static MenuType<RichFarmerMenu> TYPE;
    public static MenuType<PaddyFarmerMenu> PADDY_TYPE;

    private RichFarmerMenus() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) {
            return;
        }
        TYPE = IMenuTypeExtension.create(RichFarmerMenu::new);
        PADDY_TYPE = IMenuTypeExtension.create(PaddyFarmerMenu::new);
        event.register(Registries.MENU, ID, () -> TYPE);
        event.register(Registries.MENU, PADDY_ID, () -> PADDY_TYPE);
    }
}
