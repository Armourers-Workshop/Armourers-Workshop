package extensions.net.minecraft.client.model.PlayerModel;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.20, )")
@Extension
public class Transformer {

    public static <T extends LivingEntity> void transformFrom(@This PlayerModel<T> model, T entity, float g) {
        // dump from LivingEntityRenderer
        model.attackTime = entity.getAttackAnim(g);
        model.riding = entity.isPassenger();
        model.young = entity.isBaby();
        float h = MathUtils.rotLerp(g, entity.yBodyRotO, entity.yBodyRot);
        float j = MathUtils.rotLerp(g, entity.yHeadRotO, entity.yHeadRot);
        float k = j - h;
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity.getVehicle();
            h = MathUtils.rotLerp(g, livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
            k = j - h;
            float l = MathUtils.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathUtils.lerp(g, entity.xRotO, entity.getXRot());
        if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
            m *= -1.0f;
            k *= -1.0f;
        }
//        if (entity.hasPose(Pose.SLEEPING) && (direction = entity.getBedOrientation()) != null) {
//            n = entity.getEyeHeight(Pose.STANDING) - 0.1f;
//        }
        float l = entity.tickCount + g;
//        this.setupRotations(livingEntity, poseStack, l, h, g);
//        poseStack.scale(-1.0f, -1.0f, 1.0f);
//        this.scale(livingEntity, poseStack, g);
//        poseStack.translate(0.0, -1.501f, 0.0);
        float n = 0.0f;
        float o = 0.0f;
        if (!entity.isPassenger() && entity.isAlive()) {
            n = entity.walkAnimation.speed(g);
            o = entity.walkAnimation.position(g);
            if (entity.isBaby()) {
                o *= 3.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
        }
        model.prepareMobModel(entity, o, n, g);
        model.setupAnim(entity, o, n, l, k, m);
    }
}
