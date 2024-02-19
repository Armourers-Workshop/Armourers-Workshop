package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;

public class TridentModelArmaturePlugin extends ArmaturePlugin {

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;
        float partialTicks = context.getPartialTicks();

        IPoseStack poseStack = context.pose();
        poseStack.rotate(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot) + 90.0F));

        poseStack.rotate(Vector3f.ZP.rotationDegrees(180));
        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));

        poseStack.translate(0, -0.6875f, 0); // 0, -11, 0
    }
}
