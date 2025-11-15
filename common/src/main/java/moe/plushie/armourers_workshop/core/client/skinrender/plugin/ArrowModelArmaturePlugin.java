package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class ArrowModelArmaturePlugin extends ArmaturePlugin {

    public ArrowModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.rotateCTM(OpenVector3f.XP.rotationDegrees(-45));
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(-90));
        context.translateCTM(0, 0, -0.0625f); // 0, 0, -1
    }
}
