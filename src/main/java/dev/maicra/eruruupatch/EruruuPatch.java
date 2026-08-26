package dev.maicra.eruruupatch;

import dev.maicra.eruruupatch.command.EruruuCommands;
import dev.maicra.eruruupatch.event.BonemealEvents;
import dev.maicra.eruruupatch.event.ReinforcedPickaxeEvents;
import dev.maicra.eruruupatch.event.VillagerTradeEvents;
import dev.maicra.eruruupatch.registry.ModBlockEntities;
import dev.maicra.eruruupatch.registry.ModBlocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(EruruuPatch.MOD_ID)
public final class EruruuPatch {
    public static final String MOD_ID = "eruruu_patch";

    public EruruuPatch(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModBlockEntities::onRegisterCapabilities);

        NeoForge.EVENT_BUS.addListener(BonemealEvents::onBonemeal);
        NeoForge.EVENT_BUS.addListener(BonemealEvents::onServerTickPost);
        NeoForge.EVENT_BUS.addListener(EruruuCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(ReinforcedPickaxeEvents::onAnvilUpdate);
        NeoForge.EVENT_BUS.addListener(ReinforcedPickaxeEvents::onEnchantmentLevelSet);
        NeoForge.EVENT_BUS.addListener(ReinforcedPickaxeEvents::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(ReinforcedPickaxeEvents::onPlayerTickPost);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, VillagerTradeEvents::onVillagerTrades);
    }
}
