package moe.plushie.armourers_workshop.compat.client.renderer.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractHumanoidArmourModel;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractHumanoidModel;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractHumanoidModelProvider;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractHumanoidEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends AbstractHumanoidModel<T, S>, A extends AbstractHumanoidArmourModel<T, S>> extends AbstractHumanoidEntityRendererImpl<T, S, M, A> implements IEntityRenderer<T, S> {

    protected boolean isSlim = false;

    public AbstractHumanoidEntityRenderer(Context context, Function<AbstractHumanoidModel.Context, M> entityModelProvider, Function<AbstractHumanoidArmourModel.Context, A> entityArmourModelProvider, float shadowRadius) {
        super(context, AbstractHumanoidModelProvider.create(AbstractEntityRenderer.unwrap(context), entityModelProvider, entityArmourModelProvider), shadowRadius);
    }

    protected void setModelVisible(boolean newValue) {
        getModel().setAllVisible(newValue);
    }

    protected void setSlimModel(boolean newValue) {
        if (isSlim == newValue) {
            return; // ignore when not changed.
        }
        this.isSlim = newValue;
        if (newValue) { // is slm
            model = slimModel;
            replaceTo(normalArmorLayer, slimArmorLayer);
        } else {
            model = normalModel;
            replaceTo(slimArmorLayer, normalArmorLayer);
        }
    }

    private void replaceTo(RenderLayer<T, M> fromLayer, RenderLayer<T, M> toLayer) {
        int index = layers.indexOf(fromLayer);
        if (index >= 0) {
            layers[index] = toLayer;
        }
    }
}
