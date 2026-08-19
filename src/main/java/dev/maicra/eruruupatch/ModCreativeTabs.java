package dev.maicra.eruruupatch;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EruruuPatch.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.eruruu_patch.main"))
                    .icon(() -> new ItemStack(ModItems.ERURUU_ICON.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.FERTILIZER.get());
                        output.accept(ModItems.ENDLESS_CHARCOAL.get());
                        output.accept(ModItems.CRIMSON_CULTURE.get());
                        output.accept(ModItems.WARPED_CULTURE.get());
                        output.accept(ModItems.MOSS_HELMET.get());
                        output.accept(ModItems.SUGAR_BLOCK.get());
                        output.accept(ModItems.SUGAR_PICKAXE.get());
                        output.accept(ModItems.ENCHANTED_SUGAR_PICKAXE.get());
                        // The migrated Cutter is now owned by Easy Farmer's Delight Compat 1.1.0.
                        // Legacy eruruu_patch:cutter items remain registered for world compatibility.
                    })
                    .build());

    private ModCreativeTabs() {}
}
