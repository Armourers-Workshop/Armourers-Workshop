package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.compatibility.client.layer.AbstractSkinnableLayers;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.layer.PlaceholderLayer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultLayerArmaturePlugin extends ArmaturePlugin {

    private final ArrayList<Entry> entries = new ArrayList<>();
    private final ArrayList<EntryImpl<?, ?>> applying = new ArrayList<>();

    public static DefaultLayerArmaturePlugin villager(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(AbstractSkinnableLayers.VILLAGER_PROFESSION, plugin::whenHeadVisible);
        context.addEntityModelListener(plugin::setEntityModel);
        context.addEntityRendererListener(plugin::setEntityRenderer);
        return plugin;
    }

    public static DefaultLayerArmaturePlugin slime(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(AbstractSkinnableLayers.SLIME_OUTER, plugin::whenAnyVisible);
        context.addEntityModelListener(plugin::setEntityModel);
        context.addEntityRendererListener(plugin::setEntityRenderer);
        return plugin;
    }

    public static DefaultLayerArmaturePlugin mob(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(AbstractSkinnableLayers.STRAY_CLOTHING, plugin::whenBodyVisible);
        plugin.register(AbstractSkinnableLayers.DROWNED_OUTER, plugin::whenBodyVisible);
        context.addEntityModelListener(plugin::setEntityModel);
        context.addEntityRendererListener(plugin::setEntityRenderer);
        return plugin;
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        entries.forEach(it -> {
            if (!it.tester.get()) {
                it.impl.activate();
                applying.add(it.impl);
            }
        });
    }

    @Override
    public void deactivate(Entity entity, SkinRenderContext context) {
        applying.forEach(EntryImpl::deactivate);
        applying.clear();
    }

    @Override
    public boolean freeze() {
        entries.removeIf(it -> it.tester == null || it.impl == null);
        return !entries.isEmpty();
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void apply(LivingEntityRenderer<T, M> entityRenderer) {
        var layers = entityRenderer.layers;
        for (var targetLayer : layers) {
            for (var entry : entries) {
                if (!entry.layerClass.isInstance(targetLayer)) {
                    continue;
                }
                var impl = new EntryImpl<T, M>();
                impl.target = targetLayer;
                impl.placeholder = new PlaceholderLayer<>(entityRenderer);
                impl.layers = () -> entityRenderer.layers;
                entry.impl = impl;
            }
        }
    }

    private void setEntityModel(IModel model) {
        entries.forEach(it -> it.tester = it.testFactory.apply(model));
    }

    private void setEntityRenderer(EntityRenderer<?> entityRenderer) {
        if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
            apply(livingEntityRenderer);
        }
    }

    private void register(Class<?> clazz, Function<IModel, Supplier<Boolean>> testFactory) {
        if (clazz != null) {
            var entry = new Entry();
            entry.layerClass = clazz;
            entry.testFactory = testFactory;
            entries.add(entry);
        }
    }

    private Supplier<Boolean> whenHeadVisible(IModel model) {
        var modelPart = model.getPart("head");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    private Supplier<Boolean> whenAnyVisible(IModel model) {
        for (var part : model.getAllParts()) {
            return part::isVisible;
        }
        return null;
    }

    private Supplier<Boolean> whenBodyVisible(IModel model) {
        var modelPart = model.getPart("body");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    private static class Entry {

        Class<?> layerClass;
        Function<IModel, Supplier<Boolean>> testFactory;
        Supplier<Boolean> tester;
        EntryImpl<?, ?> impl;
    }

    private static class EntryImpl<T extends Entity, M extends EntityModel<T>> {

        RenderLayer<T, M> target;
        RenderLayer<T, M> placeholder;
        Supplier<List<RenderLayer<T, M>>> layers;
        int lastIndex = 0;

        private void activate() {
            set(target, placeholder);
        }

        private void deactivate() {
            set(placeholder, target);
        }

        private void set(RenderLayer<T, M> from, RenderLayer<T, M> to) {
            // we prioritize quick search.
            List<RenderLayer<T, M>> layers = this.layers.get();
            if (lastIndex < layers.size()) {
                if (layers.get(lastIndex) == from) {
                    layers.set(lastIndex, to);
                    return;
                }
            }
            // can't hit cache, search again.
            for (int index = 0; index < layers.size(); ++index) {
                if (layers.get(index) == from) {
                    layers.set(index, to);
                    lastIndex = index;
                    break;
                }
            }
        }
    }
}
