package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.client.Minecraft;

public class FishingModelArmaturePlugin extends ArmaturePlugin {

    public FishingModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        var rotation = Minecraft.getInstance().getCameraOrientation().eulerAnglesYXZ();
        context.rotateCTM(OpenQuaternionf.fromEulerAnglesYXZ(rotation.y(), 0, 0));
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(180.0f));
        context.translateCTM(0.03125f, 0.1875f, 0); // 0.5, 3, 0
    }
}
