package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractItemProperties;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkinItemModelManager {

    private static final SkinItemModelManager INSTANCE = new SkinItemModelManager();

    private SkinItemModel missingModel;

    private final Map<SkinType, SkinItemModel> typedItemModels = new ConcurrentHashMap<>();
    private final Map<IResourceLocation, SkinItemModel> namedItemModels = new ConcurrentHashMap<>();

    private final Map<IResourceLocation, SkinItemProperty> namedItemProperties = Collections.immutableMap(it -> {
        it.put(ModConstants.key("is_skin"), handOnly("armourers_workshop:is_skin"));
        it.put(ModConstants.key("is_crossbow"), vanilla("armourers_workshop:is_crossbow"));
        it.put(ModConstants.key("is_blocking"), vanilla("minecraft:blocking"));
        it.put(ModConstants.key("is_throwing"), vanilla("minecraft:throwing"));
    });

    public static SkinItemModelManager getInstance() {
        return INSTANCE;
    }

    public static void init() {
        var loader = new SimpleLoader();
        loader.load(INSTANCE);
    }


    public SkinItemModel getModel(SkinType skinType) {
        return typedItemModels.computeIfAbsent(skinType, it -> {
            var id = ModConstants.key("skin/" + skinType.registryName().path());
            return namedItemModels.getOrDefault(id, missingModel);
        });
    }

    @Nullable
    public SkinItemProperty getProperty(IResourceLocation id) {
        return namedItemProperties.get(id);
    }

    private static SkinItemProperty vanilla(String id) {
        var property = AbstractItemProperties.getProperty(OpenResourceLocation.parse(id));
        return new SkinItemProperty() {
            @Override
            public float call(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags, OpenItemDisplayContext displayContext) {
                return property.call(itemStack, entity, level, flags, displayContext);
            }

            @Override
            public String toString() {
                return id;
            }
        };
    }

    private static SkinItemProperty handOnly(String id) {
        var property = vanilla(id);
        return new SkinItemProperty() {
            @Override
            public float call(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags, OpenItemDisplayContext displayContext) {
                // ignore head,ground,fixed,gui
                if (displayContext.isFirstPerson() || displayContext.isThirdPerson()) {
                    return property.call(itemStack, entity, level, flags, displayContext);
                }
                return 0;
            }

            @Override
            public String toString() {
                return property.toString();
            }
        };
    }

    private static class SimpleLoader {

        private final IResourceManager resourceManager = EnvironmentManager.getClientResourceManager();

        private final Map<IResourceLocation, SimpleBuilder> builders = new LinkedHashMap<>();
        private final Map<IResourceLocation, SkinItemModel> models = new LinkedHashMap<>();

        public void load(SkinItemModelManager modelManager) {
            resourceManager.readResources(ModConstants.key("models/skin"), s -> s.endsWith(".json"), (location, resource) -> {
                var object = JsonSerializer.readFromResource(resource);
                if (object == null) {
                    return;
                }
                var path = FileUtils.getRegistryName(location.path(), "models/");
                var location1 = location.withPath(FileUtils.removeExtension(path));
                var builder = builders.computeIfAbsent(location1, SimpleBuilder::new);
                object.get("parent").ifPresent(it -> {
                    var key = OpenResourceLocation.parse(it.stringValue());
                    builder.parent = builders.computeIfAbsent(key, SimpleBuilder::new);
                });
                object.get("display").entrySet().forEach(entry -> {
                    var name = entry.getKey();
                    var value = entry.getValue();
                    var translation = parseVector3f(value.get("translation"), OpenVector3f.ZERO);
                    var rotation = parseVector3f(value.get("rotation"), OpenVector3f.ZERO);
                    var scale = parseVector3f(value.get("scale"), OpenVector3f.ONE);
                    builder.addTransform(name, new SimpleTransform(translation, rotation, scale));
                });
                object.get("overrides").allValues().forEach(it -> {
                    var model = it.get("model").stringValue();
                    var predicate = new ArrayList<Pair<IResourceLocation, Number>>();
                    it.get("predicate").entrySet().forEach(entry -> {
                        // the value only is double.
                        var key = ModConstants.key(entry.getKey());
                        var value = entry.getValue().numberValue();
                        predicate.add(Pair.of(key, value));
                    });
                    builder.addOverride(model, predicate);
                });
            });
            // resolve the parent depends.
            var references = new IdentityHashMap<SkinItemOverride, IResourceLocation>();
            builders.forEach((name, builder) -> {
                var itemModel = builder.build(references);
                models.put(name, itemModel);
            });
            references.forEach((override, reference) -> override.setModel(models.get(reference)));
            // setup the missing item model.
            var missingModel = models.get(ModConstants.key("skin/unknown"));
            if (missingModel == null) {
                throw new RuntimeException("Can't find missing model, some think wrong!");
            }
            modelManager.namedItemModels.putAll(models);
            modelManager.missingModel = missingModel;
        }

        private OpenVector3f parseVector3f(IODataObject value, OpenVector3f defaultValue) {
            if (value.isNull()) {
                return defaultValue;
            }
            if (value.size() != 3) {
                throw new RuntimeException("Expected 3 double values, found: " + value.allValues());
            }
            float x = value.at(0).floatValue();
            float y = value.at(1).floatValue();
            float z = value.at(2).floatValue();
            return new OpenVector3f(x, y, z);
        }
    }

    private static class SimpleBuilder {

        private SimpleBuilder parent;

        private final IResourceLocation name;

        private final Map<IResourceLocation, List<Pair<IResourceLocation, Number>>> overrides = new LinkedHashMap<>();
        private final Map<OpenItemDisplayContext, OpenItemTransform> transforms = new LinkedHashMap<>();

        public SimpleBuilder(IResourceLocation name) {
            this.name = name;
        }

        public void addOverride(String name, List<Pair<IResourceLocation, Number>> predicate) {
            overrides.put(OpenResourceLocation.parse(name), predicate);
        }

        public void addTransform(String name, OpenItemTransform transform) {
            transforms.put(OpenItemDisplayContext.byName(name), transform);
        }

        public SkinItemModel build(Map<SkinItemOverride, IResourceLocation> references) {
            // ..
            var itemOverrides = new ArrayList<SkinItemOverride>();
            for (var override : overrides.entrySet()) {
                var it = override.getValue();
                var properties = new SkinItemProperty[it.size()];
                var values = new float[it.size()];
                for (int i = 0; i < properties.length; i++) {
                    properties[i] = getInstance().getProperty(it.get(i).getKey());
                    values[i] = it.get(i).getValue().floatValue();
                }
                var itemOverride = new SkinItemOverride(properties, values);
                itemOverrides.add(itemOverride);
                references.put(itemOverride, override.getKey());
            }
            // ..
            var itemTransforms = new EnumMap<OpenItemDisplayContext, OpenItemTransform>(OpenItemDisplayContext.class);
            for (var displayContext : OpenItemDisplayContext.values()) {
                itemTransforms.put(displayContext, resolveTransformValue(displayContext, OpenItemTransform.NO_TRANSFORM));
            }
            return new SkinItemModel(name, itemOverrides, itemTransforms);
        }

        private OpenItemTransform resolveTransformValue(OpenItemDisplayContext transformType, OpenItemTransform defaultValue) {
            var transform = transforms.get(transformType);
            if (transform != null) {
                return transform;
            }
            if (parent != null) {
                return parent.resolveTransformValue(transformType, defaultValue);
            }
            return defaultValue;
        }
    }

    private static class SimpleTransform extends OpenItemTransform {

        public SimpleTransform(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
            super(scale(translation, new OpenVector3f(-1, -1, 1), OpenVector3f.ZERO), scale(rotation, new OpenVector3f(-1, -1, 1), OpenVector3f.ZERO), optimize(scale, OpenVector3f.ONE));
        }

        @Override
        public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
            super.apply(applyLeftHandTransform, poseStack);
            // the skin need automatic mirror model.
            if (applyLeftHandTransform) {
                poseStack.scale(-1, 1, 1);
            }
        }

        private static float scale(float a, float b) {
            if (a != 0) {
                return a * b;
            }
            return a;
        }

        private static OpenVector3f scale(OpenVector3f value, OpenVector3f scale, OpenVector3f defaultValue) {
            var x = scale(value.x(), scale.x());
            var y = scale(value.y(), scale.y());
            var z = scale(value.z(), scale.z());
            return optimize(new OpenVector3f(x, y, z), defaultValue);
        }
    }
}
