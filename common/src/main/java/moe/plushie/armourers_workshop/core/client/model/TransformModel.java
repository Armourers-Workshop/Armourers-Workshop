package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractPlayerModel;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import moe.plushie.armourers_workshop.utils.Accessor;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(value= EnvType.CLIENT)
public class TransformModel<T extends LivingEntity> extends AbstractPlayerModel<T> {

    public TransformModel(float scale) {
        this(RendererManager.getEntityContext(), scale);
    }

    protected TransformModel(AbstractEntityRendererContext context, float scale) {
        super(context, scale, false);
    }

    public void setup(T entity, int light, float partialRenderTick) {
        // dump from LivingEntityRenderer
        // FIXME:@SAGESSE
        //boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null);
        young = entity.isBaby();
        attackTime = entity.getAttackAnim(partialRenderTick);
        riding = shouldSit;
        young = entity.isBaby();

        float f = MathUtils.rotLerp(partialRenderTick, entity.yBodyRotO, entity.yBodyRot);
        float f1 = MathUtils.rotLerp(partialRenderTick, entity.yHeadRotO, entity.yHeadRot);
        float f2 = f1 - f;
        if (entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity.getVehicle();
            f = MathUtils.rotLerp(partialRenderTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = MathUtils.wrapDegrees(f2);
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
        float f6 = MathUtils.lerp(partialRenderTick, entity.xRotO, Accessor.getXRot(entity));
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entity.isAlive()) {
            f8 = MathUtils.lerp(partialRenderTick, entity.animationSpeedOld, entity.animationSpeed);
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
