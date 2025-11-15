package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractHumanoidEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.client.render.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModTextures;

@OnlyIn(Dist.CLIENT)
public class MannequinEntityRenderer extends AbstractHumanoidEntityRenderer<MannequinEntity, MannequinRenderState, MannequinModel, MannequinArmorModel> {

    public static boolean enableLimitScale = false;
    public static boolean enableLimitYRot = false;

    public MannequinEntityRenderer(Context context) {
        super(context, MannequinModel::new, MannequinArmorModel::new, 0.0f);
    }

    @Override
    protected void abi$render(MannequinRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        this.setSlimModel(renderState.isSlimModel());
        this.setModelVisible(renderState.isModelVisible());
        super.abi$render(renderState, lightmap, overlay, context);
        if (ModDebugger.mannequinCulling) {
            var box = renderState.boundingBoxForCulling();
            var tx = -box.minX - (box.maxX - box.minX) / 2;
            var ty = -box.minY;
            var tz = -box.minZ - (box.maxZ - box.minZ) / 2;
            context.draw(ShapeElement.stroke(box.move(tx, ty, tz), Colors.YELLOW));
        }
    }

    @Override
    protected boolean abi$shouldShowName(MannequinEntity entity, double d) {
        return entity.hasCustomName();
    }

    @Override
    protected float abi$getEntityScale(MannequinRenderState renderState) {
        var scale = 0.9375f; // from player renderer (maybe 15/16)
        if (!enableLimitScale) {
            scale *= renderState.scale();
        }
        return scale;
    }

    @Override
    protected OpenResourceLocation abi$getTextureLocation(MannequinRenderState renderState) {
        if (renderState.entityTexture() != null) {
            return renderState.entityTexture();
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }
}
