package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModDebugger;

@OnlyIn(Dist.CLIENT)
public class SeatEntityRenderer extends AbstractEntityRenderer<SeatEntity, EntityRenderState> {

    public SeatEntityRenderer(Context context) {
        super(context);
    }

    @Override
    protected void abi$render(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (ModDebugger.skinnable) {
            context.draw(ShapeElement.arrow());
            context.draw(ShapeElement.stroke(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, Colors.ORANGE));
        }
    }

    @Override
    protected boolean abi$shouldShowName(SeatEntity entity, double d) {
        return false; // never show name.
    }
}
