package dev.maicra.eruruupatch.integration.jade;

import dev.maicra.eruruupatch.block.CutterBlock;
import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Optional Jade integration. Nothing in the core mod requires Jade at runtime. */
@WailaPlugin("jade")
public final class EruruuJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CutterJadeProvider.INSTANCE, CutterBlockEntity.class);
        registration.registerBlockDataProvider(FarmerKnifeJadeProvider.INSTANCE, CompatFarmerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CutterJadeProvider.INSTANCE, CutterBlock.class);
        registration.registerBlockComponent(FarmerKnifeJadeProvider.INSTANCE, CompatFarmerBlock.class);
    }
}
