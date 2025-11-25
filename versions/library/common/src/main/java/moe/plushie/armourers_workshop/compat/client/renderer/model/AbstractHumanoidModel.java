package moe.plushie.armourers_workshop.compat.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractHumanoidModel<T extends LivingEntity, S extends LivingEntityRenderState> extends AbstractHumanoidModelImpl<T> {

    private static final DataContainer.Key<ColorHolder> KEY = DataContainer.key("ColorHolder", ColorHolder::new);

    public AbstractHumanoidModel(Context context) {
        super(context);
    }

    public void setupDefault(S renderState) {
        this.young = false;
        this.crouching = false;
        this.riding = false;
        // ..
        AbstractRenderState.<T, S>unwrap(renderState, (entity, f, g) -> {
            prepareMobModel(entity, 0, 0, 0);
            setupAnim(entity, 0, 0, 0, 0, 0);
        });
    }

    protected void abi$setupAnim(S renderState) {
        var holder = DataContainer.of(renderState, KEY);
        AbstractRenderState.<T, S>unwrap(renderState, (entity, f1, g1) -> {
            super.setupAnim(entity, holder.f, holder.g, holder.h, holder.i, holder.j);
        });
    }

    @Override
    public final void setupAnim(T entity, float f, float g, float h, float i, float j) {
        S renderState = AbstractRenderState.wrap(entity);
        DataContainer.of(renderState, KEY).set(f, g, h, i, j);
        abi$setupAnim(renderState);
        // copy the parent part pose, because the 1.16.5-1.21.1 no child/parent dependency.
        this.hat.copyFrom(this.head);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
    }

    protected static class ColorHolder {

        private float f;
        private float g;
        private float h;
        private float i;
        private float j;

        void set(float f, float g, float h, float i, float j) {
            this.f = f;
            this.g = g;
            this.h = h;
            this.i = i;
            this.j = j;
        }
    }
}
