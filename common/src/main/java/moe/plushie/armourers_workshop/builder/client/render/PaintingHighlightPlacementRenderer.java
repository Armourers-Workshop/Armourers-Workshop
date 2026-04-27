package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.ICamera;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTypes;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.utils.Colors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@OnlyIn(Dist.CLIENT)
public class PaintingHighlightPlacementRenderer {

    public static void renderPaintTool(ItemStack itemStack, Player player, BlockHitResult traceResult, ICamera camera, IGraphicsContext context) {
        var level = player.level();
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

        context.saveGraphicsState();

        var origin = camera.position();

        context.translateCTM(-origin.x(), -origin.y(), -origin.z());
        context.translateCTM(0.5f, 0.5f, 0.5f);

        for (var pos1 : blockSamples) {
            var x0 = pos1.getX() - 0.5f;
            var y0 = pos1.getY() - 0.5f;
            var z0 = pos1.getZ() - 0.5f;
            context.draw(ShapeElement.stroke(x0, y0, z0, 1.0f, 1.0f, 1.0f, Colors.RED, SkinRenderTypes.HIGHLIGHTED_LINES));
        }

        for (var pos1 : blockEffects) {
            var x0 = pos1.getX() - 0.4f;
            var y0 = pos1.getY() - 0.4f;
            var z0 = pos1.getZ() - 0.4f;
            context.draw(ShapeElement.stroke(x0, y0, z0, 0.8f, 0.8f, 0.8f, Colors.GREEN, SkinRenderTypes.HIGHLIGHTED_LINES));
        }

        context.restoreGraphicsState();
    }
}
