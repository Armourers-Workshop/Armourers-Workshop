package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.compatibility.client.layer.AbstractSkinnableLayers;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.layer.PlaceholderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DefaultLayerArmaturePlugin extends ArmaturePlugin {

    private ArmatureTransformerContext context;

    protected final ArrayList<Applier<?, ?>> applying = new ArrayList<>();

    public DefaultLayerArmaturePlugin(ArmatureTransformerContext context) {
        this.context = context;
    }

    public static DefaultLayerArmaturePlugin any(ArmatureTransformerContext context) {
        return new Blacklist(context, DefaultLayerArmaturePlugin::whenAnyVisible);
    }

    public static DefaultLayerArmaturePlugin villager(ArmatureTransformerContext context) {
        var plugin = new Whitelist(context);
        plugin.register(AbstractSkinnableLayers.VILLAGER_PROFESSION, DefaultLayerArmaturePlugin::whenHeadVisible);
        return plugin;
    }


    public static DefaultLayerArmaturePlugin mob(ArmatureTransformerContext context) {
        var plugin = new Whitelist(context);
        plugin.register(AbstractSkinnableLayers.STRAY_CLOTHING, DefaultLayerArmaturePlugin::whenBodyVisible);
        plugin.register(AbstractSkinnableLayers.DROWNED_OUTER, DefaultLayerArmaturePlugin::whenBodyVisible);
        return plugin;
    }

    public static BooleanSupplier whenHeadVisible(IModel model) {
        var modelPart = model.getPart("head");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    public static BooleanSupplier whenAnyVisible(IModel model) {
        for (var part : model.getAllParts()) {
            return part::isVisible;
        }
        return null;
    }

    public static BooleanSupplier whenBodyVisible(IModel model) {
        var modelPart = model.getPart("body");
        if (modelPart != null) {
            return modelPart::isVisible;
        }
        return null;
    }

    public abstract <T extends LivingEntity, M extends EntityModel<T>> Selector search(RenderLayer<T, M> layer);

    @Override
    public void activate(Entity entity, Context context) {
        applying.forEach(Applier::activate);
    }

    @Override
    public void deactivate(Entity entity, Context context) {
        applying.forEach(Applier::deactivate);
    }

    @Override
    public boolean freeze() {
        // when requires to freeze, we need to attach the layer to the renderer.
        if (context != null) {
            apply(context.getEntityModel(), context.getEntityRenderer());
            context = null;
        }
        return !applying.isEmpty();
    }

    private void apply(IModel entityModel, EntityRenderer<?> entityRenderer) {
        // bind layer to renderer.
        if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
            apply(livingEntityRenderer);
        }
        // bind the entity model to tester.
        if (entityModel != null) {
            applying.forEach(it -> it.selector.tester = it.selector.testFactory.apply(entityModel));
            applying.removeIf(it -> it.selector.tester == null);
        }
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void apply(LivingEntityRenderer<T, M> entityRenderer) {
        for (var layer : entityRenderer.layers) {
            var entry = search(layer);
            if (entry == null) {
                continue;
            }
            var applier = new Applier<T, M>(entry);
            applier.target = layer;
            applier.placeholder = new PlaceholderLayer<>(entityRenderer);
            applier.layers = () -> entityRenderer.layers;
            applying.add(applier);
        }
    }

    public static class Selector {

        private final Class<?> layerClass;
        private final Function<IModel, BooleanSupplier> testFactory;

        private BooleanSupplier tester;

        public Selector(Class<?> layerClass, Function<IModel, BooleanSupplier> testFactory) {
            this.layerClass = layerClass;
            this.testFactory = testFactory;
        }
    }

    public static class Applier<T extends Entity, M extends EntityModel<T>> {

        private final Selector selector;
        private RenderLayer<T, M> target;
        private RenderLayer<T, M> placeholder;
        private Supplier<List<RenderLayer<T, M>>> layers;
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

        private void replace(RenderLayer<T, M> from, RenderLayer<T, M> to) {
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

        public void register(Class<?> clazz, Function<IModel, BooleanSupplier> testFactory) {
            if (clazz != null) {
                selectors.add(new Selector(clazz, testFactory));
            }
        }

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> Selector search(RenderLayer<T, M> layer) {
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

        protected final Function<IModel, BooleanSupplier> testFactory;

        public Blacklist(ArmatureTransformerContext context, Function<IModel, BooleanSupplier> testFactory) {
            super(context);
            this.testFactory = testFactory;
        }

        public void register(Class<?> clazz) {
            blocked.add(clazz);
        }

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> Selector search(RenderLayer<T, M> layer) {
            for (var layerClass : blocked) {
                if (layerClass.isInstance(layer)) {
                    return null; // yep, we found it, ignore.
                }
            }
            return new Selector(layer.getClass(), testFactory);
        }
    }
}
