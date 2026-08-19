package dev.maicra.eruruupatch.client;

import dev.maicra.eruruupatch.menu.RichFarmerMenu;
import dev.maicra.eruruupatch.menu.RichFarmerMenus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class RichFarmerScreen extends AbstractContainerScreen<RichFarmerMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            "easy_villagers",
            "textures/gui/container/output.png"
    );

    public RichFarmerScreen(RichFarmerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 133;
        inventoryLabelY = 40;
        titleLabelY = 9;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
        // Reuse Easy Villagers' own 18x18 slot frame for the extra Knife slot.
        graphics.blit(BACKGROUND, x + 141, y + 19, 51, 19, 18, 18);
    }

    @EventBusSubscriber(modid = "eruruu_patch", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(RichFarmerMenus.TYPE, RichFarmerScreen::new);
            event.register(RichFarmerMenus.PADDY_TYPE, PaddyFarmerScreen::new);
        }
    }
}
