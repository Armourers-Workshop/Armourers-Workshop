package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.blockentity.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.state.HologramProjectorRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModDebugger;

@OnlyIn(Dist.CLIENT)
public class HologramProjectorBlockRenderer<T extends HologramProjectorBlockEntity, S extends HologramProjectorRenderState> extends AbstractBlockEntityRenderer<T, S> {

    public HologramProjectorBlockRenderer(Context context) {
        super(context);
    }

    @Override
    protected int abi$getViewDistance() {
        return 272;
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (!renderState.isPowered()) {
            return;
        }
        var model = renderState.slots();
        if (model.isEmpty()) {
            return; // nothing to rendering!
        }
        // ..
        if (renderState.isOverrideLight()) {
            lightmap = LightmapTexture.DEFAULT;
        }

        context.saveGraphicsState();

        context.translateCTM(0.5f, 0.5f, 0.5f);
        context.rotateCTM(renderState.renderRotations());
        context.translateCTM(0.0f, 0.5f, 0.0f);
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        apply(renderState, model, context);

        model.setPartialTick(renderState.partialTick());
        model.setAnimationTick(renderState.animationTick());
        model.setAnimationManager(renderState.animationManager());
        model.setOutlineColor(0); // never show outline in the hologram projector block.

        model.render(null, null, lightmap, overlay, context);

        context.restoreGraphicsState();

        var renderShape = renderState.renderShape();
        if (renderShape != null) {
            context.draw(ShapeElement.stroke(renderShape, Colors.ORANGE));
        }
    }

    private void apply(S renderState, SkinRenderState skin, IGraphicsContext context) {
        var animationTick = renderState.animationTick();
        var angle = renderState.modelAngle();
        var offset = renderState.modelOffset();
        var rotationOffset = renderState.rotationOffset();
        var rotationSpeed = renderState.rotationSpeed();

        var rotX = angle.x();
        var speedX = rotationSpeed.x() / 1000f;
        if (speedX != 0) {
            rotX += (float) (((animationTick % speedX) / speedX) * 360.0);
        }

        var rotY = angle.y();
        var speedY = rotationSpeed.y() / 1000f;
        if (speedY != 0) {
            rotY += (float) (((animationTick % speedY) / speedY) * 360.0);
        }

        var rotZ = angle.z();
        var speedZ = rotationSpeed.z() / 1000f;
        if (speedZ != 0) {
            rotZ += (float) (((animationTick % speedZ) / speedZ) * 360.0);
        }

        var scale = renderState.modelScale();
        context.scaleCTM(scale, scale, scale);
        if (renderState.isOverrideOrigin()) {
            var rect = OpenRectangle3f.ZERO;
            for (var slot : skin.slots()) {
                rect = slot.skin().renderBounds();
            }
            context.translateCTM(0, -rect.maxY(), 0); // to model center
        }
        context.translateCTM(-offset.x(), -offset.y(), offset.z());

        if (renderState.shouldShowRotationPoint()) {
            context.draw(ShapeElement.stroke(-1, -1, -1, 2, 2, 2, Colors.MAGENTA));
        }

        if (ModDebugger.hologramProjector) {
            context.draw(ShapeElement.arrow(0, 0, 0, 128, 128, 128));
        }

        context.rotateCTM(new OpenQuaternionf(rotX, -rotY, rotZ, true));
        context.translateCTM(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (ModDebugger.hologramProjector) {
            context.draw(ShapeElement.arrow(0, 0, 0, 128, 128, 128));
        }
    }
}
