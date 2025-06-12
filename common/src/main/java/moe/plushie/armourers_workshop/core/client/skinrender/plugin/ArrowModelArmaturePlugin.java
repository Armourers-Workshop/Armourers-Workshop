package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.world.entity.Entity;

public class ArrowModelArmaturePlugin extends ArmaturePlugin {

    public ArrowModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, Context context) {
        var poseStack = context.poseStack();
        poseStack.rotate(OpenVector3f.XP.rotationDegrees(-45));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(-90));
        poseStack.translate(0, 0, -0.0625f); // 0, 0, -1
    }
}
