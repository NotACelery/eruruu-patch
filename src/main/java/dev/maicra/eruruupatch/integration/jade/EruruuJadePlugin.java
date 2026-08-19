package dev.maicra.eruruupatch.integration.jade;

import dev.maicra.eruruupatch.block.CutterBlock;
import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Jade support retained only for legacy Eruruu Cutters already present in worlds. */
@WailaPlugin("jade")
public final class EruruuJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CutterJadeProvider.INSTANCE, CutterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CutterJadeProvider.INSTANCE, CutterBlock.class);
    }
}
