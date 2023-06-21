package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class PaintingHighlightPlacementRenderer {

    public static void renderPaintTool(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack poseStack, MultiBufferSource buffers) {
        Level level = player.getLevel();
        BlockPos pos = traceResult.getBlockPos();
        Direction direction = traceResult.getDirection();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        // must select a paintable block to preview.
        if (!(blockEntity instanceof IPaintable)) {
            return;
        }

        int radiusSample = PaintingToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = PaintingToolOptions.RADIUS_EFFECT.get(itemStack);
        boolean restrictPlane = PaintingToolOptions.PLANE_RESTRICT.get(itemStack);

        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusEffect, restrictPlane);

        poseStack.pushPose();

        Vector3f origin = new Vector3f(renderInfo.getPosition());
        poseStack.translate(-origin.getX(), -origin.getY(), -origin.getZ());
        poseStack.translate(0.5f, 0.5f, 0.5f);

        VertexConsumer builder = buffers.getBuffer(SkinRenderType.HIGHLIGHTED_LINES);

        for (BlockPos pos1 : blockSamples) {
            float x0 = pos1.getX() - 0.5f;
            float y0 = pos1.getY() - 0.5f;
            float z0 = pos1.getZ() - 0.5f;
            float x1 = pos1.getX() + 0.5f;
            float y1 = pos1.getY() + 0.5f;
            float z1 = pos1.getZ() + 0.5f;
            RenderSystem.drawBoundingBox(poseStack, x0, y0, z0, x1, y1, z1, UIColor.RED, builder);
        }

        for (BlockPos pos1 : blockEffects) {
            float x0 = pos1.getX() - 0.4f;
            float y0 = pos1.getY() - 0.4f;
            float z0 = pos1.getZ() - 0.4f;
            float x1 = pos1.getX() + 0.4f;
            float y1 = pos1.getY() + 0.4f;
            float z1 = pos1.getZ() + 0.4f;
            RenderSystem.drawBoundingBox(poseStack, x0, y0, z0, x1, y1, z1, UIColor.GREEN, builder);
        }

        poseStack.popPose();
    }
}
