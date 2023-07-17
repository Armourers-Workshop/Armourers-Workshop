package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.client.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.client.skinrender.LivingSkinRenderer;
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

@Environment(EnvType.CLIENT)
public class ForwardingLayerPlugin<T extends LivingEntity, M extends IModel> implements LivingSkinRenderer.Plugin<T, M> {

    private final ArrayList<Entry> entries = new ArrayList<>();

    public <R extends RenderLayer<?, ?>> void register(Class<R> layerType, BiPredicate<T, M> tester) {
        Entry entry = new Entry();
        entry.layerType = layerType;
        entry.layerFactory = ForwardingLayer.when(tester);
        entries.add(entry);
    }

    @Override
    public RenderLayer<T, EntityModel<T>> getOverrideLayer(LivingEntityRenderer<T, EntityModel<T>> entityRenderer, RenderLayer<T, EntityModel<T>> renderLayer) {
        for (Entry entry : entries) {
            if (entry.layerType.isInstance(renderLayer)) {
                RenderLayer<T, EntityModel<T>> newValue = entry.layerFactory.apply(entityRenderer, renderLayer);
                if (newValue != null) {
                    return newValue;
                }
            }
        }
        return null;
    }

    private class Entry {
        Class<?> layerType;
        BiFunction<RenderLayerParent<T, EntityModel<T>>, RenderLayer<T, EntityModel<T>>, RenderLayer<T, EntityModel<T>>> layerFactory;
    }
}
