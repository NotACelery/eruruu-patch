package dev.maicra.eruruupatch.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.maicra.eruruupatch.block.CutterBlock;
import dev.maicra.eruruupatch.compat.easyfarmers.CutterLogVariant;
import dev.maicra.eruruupatch.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;

/**
 * Makes Cutter item stacks visually advertise their stored log/bamboo variant.
 * The enclosure remains the normal Cutter model; a compact work-surface block is
 * rendered prominently inside it so variants are distinguishable in inventory/JEI.
 */
public final class CutterItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockRenderDispatcher blockRenderer;

    public CutterItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        poseStack.pushPose();
        blockRenderer.renderSingleBlock(
                ModBlocks.CUTTER.get().defaultBlockState().setValue(CutterBlock.FACING, Direction.SOUTH),
                poseStack,
                buffer,
                packedLight,
                packedOverlay
        );

        Block variant = CutterLogVariant.fromStack(stack);
        poseStack.pushPose();
        // Use exactly the same Farmer workstation anchor as the placed Cutter.
        // The item model now restores Minecraft's normal BlockItem display transforms,
        // so this local 0..1 block-space transform matches the world renderer.
        applyWorkTransform(poseStack, Direction.SOUTH);
        blockRenderer.renderSingleBlock(variant.defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();
    }

    /** Mirrors CutterBlockEntityRenderer / Easy Villagers Farmer crop positioning. */
    private static void applyWorkTransform(PoseStack poseStack, Direction facing) {
        final float workScale = 0.45F;
        poseStack.translate(0.5D, 1D / 16D, 0.5D);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0D, 0D, 2D / 16D);
        poseStack.translate(-0.5D, 0D, -0.5D);
        poseStack.scale(workScale, workScale, workScale);
        poseStack.translate(0.5D / workScale - 0.5D, 0D, 0.5D / workScale - 0.5D);
    }
}
