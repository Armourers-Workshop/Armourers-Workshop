package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.ICamera;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTypes;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.element.SpecialRenderElement;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.Colors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@OnlyIn(Dist.CLIENT)
public class HighlightPlacementRenderer {

    public static void renderBlock(ItemStack itemStack, Player player, BlockHitResult traceResult, ICamera camera, IGraphicsContext context) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.type() != SkinTypes.BLOCK) {
            return;
        }
        context.saveGraphicsState();

        var origin = new OpenVector3d(camera.position());
        var placeContext = new SkinBlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, traceResult);
        var location = placeContext.getClickedPos();

        context.translateCTM(location.getX() - origin.x(), location.getY() - origin.y(), location.getZ() - origin.z());
        context.translateCTM(0.5f, 0.5f, 0.5f);
        context.scaleCTM(0.0625f, 0.0625f, 0.0625f);

        for (var part : placeContext.parts()) {
            var pos = part.offset();
            var color = Colors.RED;
            if (placeContext.canPlace(part)) {
                color = Colors.WHITE;
            }
            context.saveGraphicsState();
            context.translateCTM(pos.getX() * 16, pos.getY() * 16, pos.getZ() * 16);
            context.draw(ShapeElement.stroke(part.shape(), color));
            context.restoreGraphicsState();
        }

        context.restoreGraphicsState();
    }

    public static void renderEntity(Player player, BlockHitResult traceResult, ICamera camera, IGraphicsContext context) {
        var origin = new OpenVector3d(camera.position());
        var target = MannequinHitResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        context.saveGraphicsState();

        var location = target.getLocation();

        context.translateCTM((location.x() - origin.x()), (location.y() - origin.y()), (location.z() - origin.z()));
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(-target.rotation()));

        var model = SkinItemRenderer.getMannequinModel();
        if (model instanceof IEntityModel<?> entityModel) {
            var f = target.scale() * 0.9375f; // base scale from player model
            context.saveGraphicsState();
            context.scaleCTM(f, f, f);
            context.scaleCTM(-1, -1, 1);
            context.translateCTM(0.0f, -1.501f, 0.0f);
            context.draw(SpecialRenderElement.entityModel(entityModel, MannequinRenderState.getPlaceholder(), LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, Colors.WHITE, SkinRenderTypes.HIGHLIGHTED_ENTITY_LINES));
            context.restoreGraphicsState();
        }

        context.restoreGraphicsState();
    }
}
