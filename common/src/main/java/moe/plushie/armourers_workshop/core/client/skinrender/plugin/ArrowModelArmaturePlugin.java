package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ArrowModelArmaturePlugin extends ArmaturePlugin {

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;
        float partialTicks = context.getPartialTicks();

        IPoseStack poseStack = context.pose();
        poseStack.rotate(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot)));

        AbstractArrow arrow = ObjectUtils.unsafeCast(entity);
        float f9 = (float) arrow.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathUtils.sin(f9 * 3.0F) * f9;
            poseStack.rotate(Vector3f.ZP.rotationDegrees(f10));
        }

        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));
        poseStack.translate(0, 0, -0.0625f); // 0, 0, -1
    }
}
