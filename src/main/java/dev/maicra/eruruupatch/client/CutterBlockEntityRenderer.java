package dev.maicra.eruruupatch.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.maicra.eruruupatch.block.CutterBlock;
import dev.maicra.eruruupatch.blockentity.CutterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the Cutter using Easy Villagers' Farmer workstation anchor:
 * villager behind the work surface, the crafted Log variant where the Farmer would show its crop,
 * Cutting Board directly on top of that log, and the active input on the board.
 */
public final class CutterBlockEntityRenderer implements BlockEntityRenderer<CutterBlockEntity> {
    private static final ResourceLocation CUTTING_BOARD_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting_board");
    private static final TagKey<Item> FLAT_ON_CUTTING_BOARD = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "flat_on_cutting_board")
    );
    private static final float WORK_SCALE = 0.45F;
    private static final float VILLAGER_SCALE = 0.45F;

    private final BlockRenderDispatcher blockRenderer;
    private final ItemRenderer itemRenderer;
    private final VillagerRenderer villagerRenderer;

    public CutterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.itemRenderer = minecraft.getItemRenderer();
        EntityRendererProvider.Context entityContext = new EntityRendererProvider.Context(
                minecraft.getEntityRenderDispatcher(),
                minecraft.getItemRenderer(),
                minecraft.getBlockRenderer(),
                minecraft.gameRenderer.itemInHandRenderer,
                minecraft.getResourceManager(),
                minecraft.getEntityModels(),
                minecraft.font
        );
        this.villagerRenderer = new VillagerRenderer(entityContext);
    }

    @Override
    public void render(
            CutterBlockEntity cutter,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        Direction facing = cutter.getBlockState().hasProperty(CutterBlock.FACING)
                ? cutter.getBlockState().getValue(CutterBlock.FACING)
                : Direction.SOUTH;

        renderVillager(cutter, facing, poseStack, buffer, combinedLight);
        renderWorkstation(cutter, facing, poseStack, buffer, combinedLight, combinedOverlay);
    }

    /** Mirrors Easy Villagers' Farmer villager transform. */
    private void renderVillager(
            CutterBlockEntity cutter,
            Direction facing,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight
    ) {
        Villager villager = cutter.villagerAdapter().getVillagerEntity();
        if (villager == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 1D / 16D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0D, 0D, -4D / 16D);
        poseStack.scale(VILLAGER_SCALE, VILLAGER_SCALE, VILLAGER_SCALE);
        villagerRenderer.render(villager, 0F, 1F, poseStack, buffer, combinedLight);
        poseStack.popPose();
    }

    /**
     * The log uses the exact Farmer crop anchor. The Cutting Board is translated
     * one local block upward inside the same 0.45-scaled workspace, so it sits
     * directly on the log instead of floating independently near the roof.
     */
    private void renderWorkstation(
            CutterBlockEntity cutter,
            Direction facing,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        poseStack.pushPose();
        applyWorkTransform(poseStack, facing);
        blockRenderer.renderSingleBlock(cutter.logVariant().defaultBlockState(), poseStack, buffer, light, overlay);

        Block board = BuiltInRegistries.BLOCK.get(CUTTING_BOARD_ID);
        if (board != Blocks.AIR) {
            poseStack.pushPose();
            // One local block above the scaled log: the board rests on its top face.
            poseStack.translate(0D, 1D, 0D);
            blockRenderer.renderSingleBlock(board.defaultBlockState(), poseStack, buffer, light, overlay);
            renderProcessingItem(cutter, poseStack, buffer, light, overlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    /**
     * Mirrors Farmer's Delight CuttingBoardRenderer for a single displayed unit.
     * Normal/flat-tagged items lie flat at the board's surface; 3D block items are
     * raised and rendered upright. Inventory contents remain server-authoritative.
     */
    private void renderProcessingItem(
            CutterBlockEntity cutter,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        ItemStack shown = cutter.displayInput();
        if (shown.isEmpty() || cutter.getLevel() == null) {
            return;
        }

        poseStack.pushPose();

        // Farmer's Delight itself checks the FIXED model transform to distinguish
        // 3D block models from flat items before choosing the board presentation.
        poseStack.pushPose();
        boolean isBlockItem = itemRenderer
                .getModel(shown, cutter.getLevel(), null, 0)
                .applyTransform(ItemDisplayContext.FIXED, poseStack, false)
                .isGui3d();
        poseStack.popPose();

        if (isBlockItem && !shown.is(FLAT_ON_CUTTING_BOARD)) {
            // Same single-stack placement used by Farmer's Delight renderBlock().
            poseStack.translate(0.5D, 0.30D, 0.5D);
            poseStack.scale(0.8F, 0.8F, 0.8F);
        } else {
            // Same single-stack placement used by renderItemLayingDown().
            poseStack.translate(0.5D, 0.11D, 0.5D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.6F, 0.6F, 0.6F);
        }

        itemRenderer.renderStatic(
                shown,
                ItemDisplayContext.FIXED,
                light,
                overlay,
                poseStack,
                buffer,
                cutter.getLevel(),
                cutter.getBlockPos().hashCode()
        );
        poseStack.popPose();
    }

    /** Mirrors Easy Villagers FarmerRenderer crop positioning exactly. */
    private static void applyWorkTransform(PoseStack poseStack, Direction facing) {
        poseStack.translate(0.5D, 1D / 16D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0D, 0D, 2D / 16D);
        poseStack.translate(-0.5D, 0D, -0.5D);
        poseStack.scale(WORK_SCALE, WORK_SCALE, WORK_SCALE);
        poseStack.translate(0.5D / WORK_SCALE - 0.5D, 0D, 0.5D / WORK_SCALE - 0.5D);
    }
}
