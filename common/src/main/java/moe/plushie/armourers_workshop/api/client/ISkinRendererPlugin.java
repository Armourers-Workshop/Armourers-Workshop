package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public interface ISkinRendererPlugin<T extends LivingEntity, V extends EntityModel<T>, M extends IModelHolder<V>> {

    RenderLayer<T, V> getOverrideLayer(SkinRenderer<T, V, M> skinRenderer, LivingEntityRenderer<T, V> entityRenderer, RenderLayer<T, V> renderLayer);
}
