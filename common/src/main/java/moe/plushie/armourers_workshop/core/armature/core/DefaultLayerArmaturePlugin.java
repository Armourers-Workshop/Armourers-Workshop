package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.layer.PlaceholderLayer;
import moe.plushie.armourers_workshop.core.client.render.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.utils.NamedClass;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DefaultLayerArmaturePlugin extends ArmaturePlugin {

    private IEntityModel<?> entityModel;
    private IEntityRenderer<?, ?> entityRenderer;

    protected final ArrayList<Applier<?>> applying = new ArrayList<>();

    public DefaultLayerArmaturePlugin(ArmatureTransformerContext context) {
        context.addEntityModelListener(entityModel -> this.entityModel = entityModel);
        context.addEntityRendererListener(entityRenderer -> this.entityRenderer = entityRenderer);
    }

    public static DefaultLayerArmaturePlugin any(ArmatureTransformerContext context) {
        var plugin = new Blacklist(context, DefaultLayerArmaturePlugin::whenAnyVisible);
        plugin.register(SkinWardrobeLayer.class);
        return plugin;
    }

    public static DefaultLayerArmaturePlugin villager(ArmatureTransformerContext context) {
        var plugin = new Whitelist(context);
        plugin.register("minecraft:layer/villager_profession", DefaultLayerArmaturePlugin::whenHeadVisible);
        return plugin;
    }


    public static DefaultLayerArmaturePlugin mob(ArmatureTransformerContext context) {
        var plugin = new Whitelist(context);
        plugin.register("minecraft:layer/stray_clothing", DefaultLayerArmaturePlugin::whenBodyVisible);
        plugin.register("minecraft:layer/drowned_outer", DefaultLayerArmaturePlugin::whenBodyVisible);
        return plugin;
    }

    public static BooleanSupplier whenHeadVisible(IEntityModel<?> entityModel) {
        var modelPart = entityModel.abi$getPartByName("head");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    public static BooleanSupplier whenAnyVisible(IEntityModel<?> entityModel) {
        for (var part : entityModel.abi$allParts()) {
            return part::isVisible;
        }
        return null;
    }

    public static BooleanSupplier whenBodyVisible(IEntityModel<?> entityModel) {
        var modelPart = entityModel.abi$getPartByName("body");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    public abstract <T> Selector search(T layer);

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        applying.forEach(Applier::activate);
    }

    @Override
    public void deactivate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        applying.forEach(Applier::deactivate);
    }

    @Override
    public boolean freeze() {
        // when requires to freeze, we need to attach the layer to the renderer.
        if (entityModel != null && entityRenderer != null) {
            buildRules(entityModel, entityRenderer);
            entityModel = null;
            entityRenderer = null;
        }
        return !applying.isEmpty();
    }

    private void buildRules(IEntityModel<?> entityModel, IEntityRenderer<?, ?> entityRenderer) {
        // bind layer to renderer.
        if (entityRenderer instanceof ILivingEntityRenderer<?, ?, ?> livingEntityRenderer) {
            buildRules(Objects.unsafeCast(livingEntityRenderer));
        }
        // bind the entity model to tester.
        if (entityModel != null) {
            applying.forEach(it -> it.selector.tester = it.selector.testFactory.apply(entityModel));
            applying.removeIf(it -> it.selector.tester == null);
        }
    }

    private <T extends LivingEntity, S extends LivingEntityRenderState, M extends IEntityModel<S>> void buildRules(ILivingEntityRenderer<T, S, M> entityRenderer) {
        for (var layer : entityRenderer.abi$getLayers()) {
            var entry = search(layer);
            if (entry == null) {
                continue;
            }
            var applier = new Applier<>(entry);
            applier.target = layer;
            applier.placeholder = new PlaceholderLayer<>(entityRenderer);
            applier.layers = entityRenderer::abi$getLayers;
            applying.add(applier);
        }
    }

    public static class Selector {

        private final Class<?> layerClass;
        private final Function<IEntityModel<?>, BooleanSupplier> testFactory;

        private BooleanSupplier tester;

        public Selector(Class<?> layerClass, Function<IEntityModel<?>, BooleanSupplier> testFactory) {
            this.layerClass = layerClass;
            this.testFactory = testFactory;
        }
    }

    public static class Applier<T> {

        private final Selector selector;
        private T target;
        private T placeholder;
        private Supplier<List<T>> layers;
        private int lastIndex = -1;
        private boolean isEnabled = false;

        public Applier(Selector selector) {
            this.selector = selector;
        }

        public void activate() {
            if (!isEnabled && !selector.tester.getAsBoolean()) {
                replace(target, placeholder);
                isEnabled = true;
            }
        }

        public void deactivate() {
            if (isEnabled) {
                replace(placeholder, target);
                isEnabled = false;
            }
        }

        private void replace(T from, T to) {
            // we prioritize quick search.
            var layers = this.layers.get();
            if (lastIndex >= 0 && lastIndex < layers.size()) {
                if (layers.get(lastIndex) == from) {
                    layers.set(lastIndex, to);
                    return;
                }
            }
            // can't hit cache, search again.
            for (var index = 0; index < layers.size(); ++index) {
                if (layers.get(index) == from) {
                    layers.set(index, to);
                    lastIndex = index;
                    break;
                }
            }
        }
    }

    public static class Whitelist extends DefaultLayerArmaturePlugin {

        protected final ArrayList<Selector> selectors = new ArrayList<>();

        public Whitelist(ArmatureTransformerContext context) {
            super(context);
        }

        public void register(String name, Function<IEntityModel<?>, BooleanSupplier> testFactory) {
            register(NamedClass.forName(name), testFactory);
        }

        public void register(Class<?> clazz, Function<IEntityModel<?>, BooleanSupplier> testFactory) {
            if (clazz != null) {
                selectors.add(new Selector(clazz, testFactory));
            }
        }

        @Override
        public <T> Selector search(T layer) {
            for (var entry : selectors) {
                if (entry.layerClass.isInstance(layer)) {
                    return entry;
                }
            }
            return null;
        }
    }

    public static class Blacklist extends DefaultLayerArmaturePlugin {

        protected final ArrayList<Class<?>> blocked = new ArrayList<>();

        protected final Function<IEntityModel<?>, BooleanSupplier> testFactory;

        public Blacklist(ArmatureTransformerContext context, Function<IEntityModel<?>, BooleanSupplier> testFactory) {
            super(context);
            this.testFactory = testFactory;
        }

        public void register(Class<?> clazz) {
            blocked.add(clazz);
        }

        @Override
        public <T> Selector search(T layer) {
            for (var layerClass : blocked) {
                if (layerClass.isInstance(layer)) {
                    return null; // yep, we found it, ignore.
                }
            }
            return new Selector(layer.getClass(), testFactory);
        }
    }
}
