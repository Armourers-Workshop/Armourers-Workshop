package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

public class TridentModelArmaturePlugin extends ArmaturePlugin {

    public TridentModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        auto poseStack = context.pose();

        poseStack.rotate(Vector3f.ZP.rotationDegrees(180));
        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));

        poseStack.translate(0, -0.6875f, 0); // 0, -11, 0
    }
}
