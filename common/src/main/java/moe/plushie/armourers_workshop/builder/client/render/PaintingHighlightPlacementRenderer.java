package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.compatibility.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
public class PaintingHighlightPlacementRenderer {

    public static void renderPaintTool(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, IPoseStack poseStack, IBufferSource bufferSource) {
        var level = player.getLevel();
        var pos = traceResult.getBlockPos();
        var direction = AbstractDirection.wrap(traceResult.getDirection());
        var blockEntity = level.getBlockEntity(pos);

        // must select a paintable block to preview.
        if (!(blockEntity instanceof IBlockPaintable)) {
            return;
        }

        var radiusSample = itemStack.get(PaintingToolOptions.RADIUS_SAMPLE);
        var radiusEffect = itemStack.get(PaintingToolOptions.RADIUS_EFFECT);
        var restrictPlane = itemStack.get(PaintingToolOptions.PLANE_RESTRICT);

        var blockSamples = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusSample, restrictPlane);
        var blockEffects = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusEffect, restrictPlane);

        poseStack.pushPose();

        var origin = renderInfo.getPosition();
        var builder = bufferSource.getBuffer(SkinRenderType.HIGHLIGHTED_LINES);

        poseStack.translate((float) -origin.x(), (float) -origin.y(), (float) -origin.z());
        poseStack.translate(0.5f, 0.5f, 0.5f);

        for (var pos1 : blockSamples) {
            var x0 = pos1.getX() - 0.5f;
            var y0 = pos1.getY() - 0.5f;
            var z0 = pos1.getZ() - 0.5f;
            var x1 = pos1.getX() + 0.5f;
            var y1 = pos1.getY() + 0.5f;
            var z1 = pos1.getZ() + 0.5f;
            ShapeTesselator.fill(x0, y0, z0, x1, y1, z1, UIColor.RED, poseStack, builder);
        }

        for (var pos1 : blockEffects) {
            var x0 = pos1.getX() - 0.4f;
            var y0 = pos1.getY() - 0.4f;
            var z0 = pos1.getZ() - 0.4f;
            var x1 = pos1.getX() + 0.4f;
            var y1 = pos1.getY() + 0.4f;
            var z1 = pos1.getZ() + 0.4f;
            ShapeTesselator.fill(x0, y0, z0, x1, y1, z1, UIColor.GREEN, poseStack, builder);
        }

        poseStack.popPose();
    }
}
