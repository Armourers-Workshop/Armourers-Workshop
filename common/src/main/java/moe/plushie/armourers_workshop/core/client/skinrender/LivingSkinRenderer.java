package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
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
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class LivingSkinRenderer<T extends LivingEntity, M extends IModel> extends SkinRenderer<T, M> {

    protected LivingEntityRenderer<T, ?> renderer;

    public LivingSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void init(EntityRenderer<T> entityRenderer) {
        super.init(entityRenderer);
        if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
            apply((LivingEntityRenderer<T, EntityModel<T>>) entityRenderer);
        }
    }

    @Override
    public M getOverrideModel(M model) {
        return super.getOverrideModel(getResolvedModel(model));
    }

    @Override
    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, SkinRenderContext context) {
        return super.render(entity, getResolvedModel(model), bakedSkin, scheme, context);
    }

    public M getResolvedModel(M model) {
        // we don't know how to draw without a model, right?
        if (model == null) {
            model = ModelHolder.of(renderer.getModel());
        }
        return model;
    }

    private void apply(LivingEntityRenderer<T, EntityModel<T>> entityRenderer) {
        this.renderer = entityRenderer;
        SkinRendererManager.getInstance().applyPlugins(this, plugin -> {
            auto layers = entityRenderer.layers;
            for (int index = 0; index < layers.size(); ++index) {
                auto newValue = plugin.getOverrideLayer(entityRenderer, layers.get(index));
                if (newValue != null) {
                    layers.set(index, newValue);
                }
            }
        });
    }

    public interface Plugin<T extends LivingEntity, M extends IModel> {

        RenderLayer<T, EntityModel<T>> getOverrideLayer(LivingEntityRenderer<T, EntityModel<T>> entityRenderer, RenderLayer<T, EntityModel<T>> renderLayer);
    }
}

