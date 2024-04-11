package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.layer.PlaceholderLayer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

public class DefaultLayerArmaturePlugin extends ArmaturePlugin {

    private final ArrayList<Entry> entries = new ArrayList<>();
    private final ArrayList<EntryImpl<?, ?>> applying = new ArrayList<>();

    public static DefaultLayerArmaturePlugin villager(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(VillagerProfessionLayer.class, plugin::whenHeadVisible);
        context.addEntityModelListener(plugin::setEntityModel);
        context.addEntityRendererListener(plugin::setEntityRenderer);
        return plugin;
    }

    public static DefaultLayerArmaturePlugin slime(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(SlimeOuterLayer.class, plugin::whenAnyVisible);
        context.addEntityModelListener(plugin::setEntityModel);
        context.addEntityRendererListener(plugin::setEntityRenderer);
        return plugin;
    }

    public static DefaultLayerArmaturePlugin mob(ArmatureTransformerContext context) {
        DefaultLayerArmaturePlugin plugin = new DefaultLayerArmaturePlugin();
        plugin.register(StrayClothingLayer.class, plugin::whenBodyVisible);
        plugin.register(DrownedOuterLayer.class, plugin::whenBodyVisible);
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
        auto layers = entityRenderer.layers;
        for (RenderLayer<T, M> targetLayer : layers) {
            for (Entry entry : entries) {
                if (!entry.layerClass.isInstance(targetLayer)) {
                    continue;
                }
                EntryImpl<T, M> impl = new EntryImpl<>();
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
        LivingEntityRenderer<?, ?> livingEntityRenderer = ObjectUtils.safeCast(entityRenderer, LivingEntityRenderer.class);
        if (livingEntityRenderer != null) {
            apply(livingEntityRenderer);
        }
    }

    private void register(Class<?> clazz, Function<IModel, Supplier<Boolean>> testFactory) {
        Entry entry = new Entry();
        entry.layerClass = clazz;
        entry.testFactory = testFactory;
        entries.add(entry);
    }

    private Supplier<Boolean> whenHeadVisible(IModel model) {
        IModelPart modelPart = model.getPart("head");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    private Supplier<Boolean> whenAnyVisible(IModel model) {
        for (IModelPart part : model.getAllParts()) {
            return part::isVisible;
        }
        return null;
    }

    private Supplier<Boolean> whenBodyVisible(IModel model) {
        IModelPart modelPart = model.getPart("body");
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
