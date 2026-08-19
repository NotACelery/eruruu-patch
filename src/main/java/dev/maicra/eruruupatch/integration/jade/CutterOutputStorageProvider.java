package dev.maicra.eruruupatch.integration.jade;

import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ItemViewUtils;
import snownee.jade.api.view.ViewGroup;

/**
 * Exposes only the Cutter's four finished-product slots to Jade's universal
 * item-storage renderer. This is display-only and does not alter NeoForge item
 * capabilities, hopper sidedness, or the protected tool slot.
 */
public enum CutterOutputStorageProvider implements
        IServerExtensionProvider<ItemStack>,
        IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            "eruruu_patch",
            "cutter_outputs"
    );

    @Override
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof CutterBlockEntity cutter)) {
            return null;
        }

        // Feed exactly the Cutter's finished-output handler through Jade's own
        // grouping logic. This is the same path Jade uses for ordinary item
        // storages, so counts/grouping/layout stay consistent with Rich Farmers.
        List<ViewGroup<ItemStack>> groups = ItemViewUtils.groupOf(cutter.outputHandler(), accessor);
        return groups == null || groups.isEmpty() ? null : groups;
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(
            Accessor<?> accessor,
            List<ViewGroup<ItemStack>> groups
    ) {
        return ClientViewGroup.map(groups, ItemView::new, null);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
