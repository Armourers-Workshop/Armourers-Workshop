package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.world.entity.Entity;

public class TridentModelArmaturePlugin extends ArmaturePlugin {

    public TridentModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, Context context) {
        var poseStack = context.poseStack();

        poseStack.rotate(OpenVector3f.ZP.rotationDegrees(180));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(-90));

        poseStack.translate(0, -0.6875f, 0); // 0, -11, 0
    }
}
