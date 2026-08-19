package dev.maicra.eruruupatch.integration.jade;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.maicra.eruruupatch.compat.easyfarmers.KnifeHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** Adds the equipped Rich Farmer / Rich Paddy Farmer Knife to Jade. */
public enum FarmerKnifeJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            "eruruu_patch",
            "farmer_knife"
    );
    private static final String KEY_KNIFE = "EruruuFarmerKnife";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        data.remove(KEY_KNIFE);

        if (!(accessor.getBlockEntity() instanceof CompatFarmerBlockEntity farmer)
                || !farmer.variant().isRich()) {
            return;
        }

        ItemStack knife = ((KnifeHolder) (Object) farmer).eruruu$getKnife();
        if (!knife.isEmpty()) {
            data.put(KEY_KNIFE, knife.save(accessor.getLevel().registryAccess()));
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(KEY_KNIFE, Tag.TAG_COMPOUND)) {
            return;
        }

        ItemStack knife = ItemStack.parseOptional(
                accessor.getLevel().registryAccess(),
                data.getCompound(KEY_KNIFE)
        );
        if (knife.isEmpty()) {
            return;
        }

        tooltip.add(Component.translatable(
                "jade.eruruu_patch.farmer.knife",
                knife.getHoverName()
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
