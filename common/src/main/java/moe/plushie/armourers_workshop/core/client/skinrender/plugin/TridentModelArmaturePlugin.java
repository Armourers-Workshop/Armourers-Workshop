package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class TridentModelArmaturePlugin extends ArmaturePlugin {

    public TridentModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.rotateCTM(OpenVector3f.ZP.rotationDegrees(180));
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(-90));

        context.translateCTM(0, -0.6875f, 0); // 0, -11, 0
    }
}
