package moe.plushie.armourers_workshop.compat.client.renderer.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.compat.client.renderer.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.20, 1.22)")
public class AbstractTransformModel<T extends LivingEntity, S extends EntityRenderState> extends AbstractHumanoidModelImpl<T> {

    public AbstractTransformModel() {
        super(getEmptyContext());
    }

    public void setup(S renderState) {
        AbstractRenderState.<T, S>unwrap(renderState, this::setup);
    }

    public void setup(T entity, float f, float g) {
        // dump from LivingEntityRenderer
        this.attackTime = entity.getAttackAnim(g);
        this.riding = entity.isPassenger();
        this.young = entity.isBaby();
        float h = OpenMath.rotLerp(g, entity.yBodyRotO, entity.yBodyRot);
        float j = OpenMath.rotLerp(g, entity.yHeadRotO, entity.yHeadRot);
        float k = j - h;
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingEntity) {
            h = OpenMath.rotLerp(g, livingEntity.yBodyRotO, livingEntity.yBodyRot);
            k = j - h;
            float l = OpenMath.wrapDegrees(k);
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
        float m = OpenMath.lerp(g, entity.xRotO, entity.getXRot());
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
        this.prepareMobModel(entity, o, n, g);
        this.setupAnim(entity, o, n, l, k, m);
    }

    public IModelPart getPartByName(String name) {
        return self().abi$getPartByName(name);
    }

    private IEntityModel<S> self() {
        return Objects.unsafeCast(this);
    }
}
