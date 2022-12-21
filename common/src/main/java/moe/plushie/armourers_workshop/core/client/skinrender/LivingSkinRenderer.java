package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

@Environment(value = EnvType.CLIENT)
public class LivingSkinRenderer<T extends LivingEntity, V extends EntityModel<T>, M extends IModelHolder<V>> extends SkinRenderer<T, V, M> {

    protected final HashMap<Class<?>, BiFunction<RenderLayerParent<T, V>, RenderLayer<T, V>, RenderLayer<T, V>>> mappers = new HashMap<>();
    protected LivingEntityRenderer<T, V> renderer;

    public LivingSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void init(EntityRenderer<T> entityRenderer) {
        super.init(entityRenderer);
        if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
            init((LivingEntityRenderer<T, V>) entityRenderer);
        }
    }

    protected void init(LivingEntityRenderer<T, V> entityRenderer) {
        this.renderer = entityRenderer;
        SkinRendererManager.getInstance().getPlugins(this).forEach(plugin -> {
            List<RenderLayer<T, V>> layers = entityRenderer.layers;
            for (int index = 0; index < layers.size(); ++index) {
                RenderLayer<T, V> newValue = plugin.getOverrideLayer(this, entityRenderer, layers.get(index));
                if (newValue != null) {
                    layers.set(index, newValue);
                }
            }
        });
    }

    @Override
    public M getOverrideModel(M model) {
        return super.getOverrideModel(getResolvedModel(model));
    }

    @Override
    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, SkinRenderContext context) {
        return super.render(entity, getResolvedModel(model), bakedSkin, scheme, context);
    }

    public V getModel() {
        return renderer.getModel();
    }

    public M getResolvedModel(M model) {
        // we don't know how to draw without a model, right?
        if (model == null) {
            model = ModelHolder.of(getModel());
        }
        return model;
    }
}

