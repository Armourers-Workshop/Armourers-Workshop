package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.core.client.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@Environment(value = EnvType.CLIENT)
public class ForwardingLayerPlugin<T extends LivingEntity, V extends EntityModel<T>, M extends IModelHolder<V>> implements SkinRenderer.Plugin<T, V, M> {

    private final ArrayList<Entry> entries = new ArrayList<>();

    public <R extends RenderLayer<?, ?>> void register(Class<R> layerType, BiPredicate<T, M> tester) {
        Entry entry = new Entry();
        entry.layerType = layerType;
        entry.layerFactory = ForwardingLayer.when(tester);
        entries.add(entry);
    }

    @Override
    public RenderLayer<T, V> getOverrideLayer(SkinRenderer<T, V, M> skinRenderer, LivingEntityRenderer<T, V> entityRenderer, RenderLayer<T, V> renderLayer) {
        for (Entry entry : entries) {
            if (entry.layerType.isInstance(renderLayer)) {
                RenderLayer<T, V> newValue = entry.layerFactory.apply(entityRenderer, renderLayer);
                if (newValue != null) {
                    return newValue;
                }
            }
        }
        return null;
    }

    private class Entry {
        Class<?> layerType;
        BiFunction<RenderLayerParent<T, V>, RenderLayer<T, V>, RenderLayer<T, V>> layerFactory;
    }
}
