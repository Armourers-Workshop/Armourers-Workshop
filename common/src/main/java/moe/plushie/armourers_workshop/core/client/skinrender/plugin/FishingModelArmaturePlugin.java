package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.world.entity.Entity;

public class FishingModelArmaturePlugin extends ArmaturePlugin {

    public FishingModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, Context context) {
        var poseStack = context.poseStack();
        var rotation = EnvironmentManager.getClient().getCameraOrientation().eulerAnglesYXZ();
        poseStack.rotate(OpenQuaternionf.fromEulerAnglesYXZ(rotation.y(), 0, 0));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(180.0f));
        poseStack.translate(0.03125f, 0.1875f, 0); // 0.5, 3, 0
    }
}
