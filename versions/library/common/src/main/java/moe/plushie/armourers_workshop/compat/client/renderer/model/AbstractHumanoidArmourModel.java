package moe.plushie.armourers_workshop.compat.client.renderer.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractHumanoidArmourModel<T extends LivingEntity, S extends LivingEntityRenderState> extends AbstractHumanoidArmourModelImpl<T> {

    private static final DataContainer.Key<ColorHolder> KEY = DataContainer.key("ColorHolder", ColorHolder::new);

    public AbstractHumanoidArmourModel(Context context) {
        super(context);
    }

    public void abi$setupAnim(S renderState) {
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
