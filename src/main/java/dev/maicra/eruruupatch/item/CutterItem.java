package dev.maicra.eruruupatch.item;

import dev.maicra.eruruupatch.client.CutterItemRenderer;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Cutter BlockItem with material tooltip and a variant-aware item renderer. */
public final class CutterItem extends BlockItem {
    public CutterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        Block variant = CutterLogVariant.fromStack(stack);
        tooltip.add(Component.translatable(
                "tooltip.eruruu_patch.cutter.variant",
                Component.translatable(CutterLogVariant.translationKey(variant))
        ).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new CutterItemRenderer();
                }
                return renderer;
            }
        });
    }
}
