package dev.maicra.eruruupatch.client.crafting;

import dev.maicra.eruruupatch.EruruuPatch;
import dev.maicra.eruruupatch.crafting.CraftingRecipeCandidate;
import dev.maicra.eruruupatch.network.SelectCraftingRecipePayload;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = EruruuPatch.MOD_ID, value = Dist.CLIENT)
public final class RecipeConflictClientEvents {
    private static final CraftingConflictClientState STATE = new CraftingConflictClientState();

    private RecipeConflictClientEvents() {
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        AbstractContainerScreen<?> screen = supportedScreen(event.getScreen());
        if (screen == null) {
            return;
        }

        CraftingConflictClientState.Snapshot snapshot = STATE.refresh(screen);
        if (snapshot == null || snapshot.choices().size() < 2) {
            return;
        }

        RecipeConflictWidget.render(
                event.getGuiGraphics(),
                screen,
                snapshot,
                event.getMouseX(),
                event.getMouseY()
        );
    }

    @SubscribeEvent
    public static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != 0) {
            return;
        }

        AbstractContainerScreen<?> screen = supportedScreen(event.getScreen());
        if (screen == null) {
            return;
        }

        CraftingConflictClientState.Snapshot snapshot = STATE.refresh(screen);
        if (snapshot == null || snapshot.choices().size() < 2) {
            return;
        }

        RecipeConflictWidget.Layout layout = RecipeConflictWidget.layout(screen, snapshot);
        if (layout.insideButton(event.getMouseX(), event.getMouseY())) {
            STATE.togglePopup();
            event.setCanceled(true);
            return;
        }

        int candidateIndex = RecipeConflictWidget.clickedChoice(
                layout,
                snapshot,
                event.getMouseX(),
                event.getMouseY()
        );
        if (candidateIndex >= 0) {
            CraftingRecipeCandidate candidate = snapshot.choices().get(candidateIndex);
            STATE.select(candidate.id());
            PacketDistributor.sendToServer(new SelectCraftingRecipePayload(
                    snapshot.menu().containerId,
                    snapshot.signature(),
                    candidate.id()
            ));
            event.setCanceled(true);
            return;
        }

        if (snapshot.popupOpen()) {
            STATE.closePopup();
        }
    }

    @SubscribeEvent
    public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        AbstractContainerScreen<?> screen = supportedScreen(event.getScreen());
        if (screen == null) {
            return;
        }

        CraftingConflictClientState.Snapshot snapshot = STATE.refresh(screen);
        if (snapshot == null || !snapshot.popupOpen()) {
            return;
        }

        RecipeConflictWidget.Layout layout = RecipeConflictWidget.layout(screen, snapshot);
        if (!layout.insidePopup(event.getMouseX(), event.getMouseY())) {
            return;
        }

        double delta = event.getScrollDeltaY();
        if (delta != 0.0) {
            STATE.scroll(delta > 0.0 ? -1 : 1, layout.visibleRows());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClosing(ScreenEvent.Closing event) {
        if (supportedScreen(event.getScreen()) != null) {
            STATE.clear();
        }
    }

    private static AbstractContainerScreen<?> supportedScreen(Object screen) {
        if (screen instanceof CraftingScreen craftingScreen) {
            return craftingScreen;
        }
        if (screen instanceof InventoryScreen inventoryScreen) {
            return inventoryScreen;
        }
        return null;
    }
}
