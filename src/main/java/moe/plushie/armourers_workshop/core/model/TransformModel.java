package moe.plushie.armourers_workshop.core.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class TransformModel<T extends LivingEntity> extends BipedModel<T> {

    public TransformModel(float scale) {
        super(scale);
    }

    public void setup(T entity, int light, float partialRenderTick) {
        // dump from LivingRenderer
        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        young = entity.isBaby();
        attackTime = entity.getAttackAnim(partialRenderTick);
        riding = shouldSit;
        young = entity.isBaby();

        float f = MathHelper.rotLerp(partialRenderTick, entity.yBodyRotO, entity.yBodyRot);
        float f1 = MathHelper.rotLerp(partialRenderTick, entity.yHeadRotO, entity.yHeadRot);
        float f2 = f1 - f;
        if (entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity.getVehicle();
            f = MathHelper.rotLerp(partialRenderTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }
        float f7 = entity.tickCount + partialRenderTick;
        float f6 = MathHelper.lerp(partialRenderTick, entity.xRotO, entity.xRot);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entity.isAlive()) {
            f8 = MathHelper.lerp(partialRenderTick, entity.animationSpeedOld, entity.animationSpeed);
            f5 = entity.animationPosition - entity.animationSpeed * (1.0F - partialRenderTick);
            if (entity.isBaby()) {
                f5 *= 3.0F;
            }
            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        prepareMobModel(entity, f5, f8, partialRenderTick);
        setupAnim(entity, f5, f8, f7, f2, f6);
    }
}
