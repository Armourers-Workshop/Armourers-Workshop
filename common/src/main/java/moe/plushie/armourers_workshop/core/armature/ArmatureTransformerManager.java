package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.client.render.model.LinkedModel;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ArmatureTransformerManager {

    private final HashMap<OpenResourceKey, ArmatureTransformerBuilder> pendingBuilders = new HashMap<>();

    private final HashMap<OpenResourceKey, ArmatureTransformerBuilder> namedBuilders = new HashMap<>();
    private final HashMap<IRegistryHolder<?>, ArrayList<ArmatureTransformerBuilder>> entityBuilders = new HashMap<>();
    private final HashMap<Class<?>, ArrayList<ArmatureTransformerBuilder>> modelBuilders = new HashMap<>();

    private int version = 0;

    protected abstract ArmatureTransformerBuilder createBuilder(OpenResourceKey name);

    public void clear() {
        pendingBuilders.clear();
    }

    public void append(OpenResourceKey registryName, IODataObject object) {
        var builder = createBuilder(registryName);
        pendingBuilders.put(registryName, builder);
        builder.load(object);
    }

    public void freeze() {
        var builders1 = new HashMap<OpenResourceKey, ArmatureTransformerBuilder>();
        pendingBuilders.forEach((name, builder) -> {
            var chain = new ArrayList<ArmatureTransformerBuilder>();
            var nextBuilder = builder;
            while (nextBuilder.parent() != null) {
                var parent = pendingBuilders.get(nextBuilder.parent());
                if (parent == null) {
                    ModLog.warn("Can't found parent '{}'", nextBuilder.parent());
                    break;
                }
                chain.add(parent);
                nextBuilder = parent;
            }
            if (!chain.isEmpty()) {
                builder.resolve(chain);
            }
            builders1.put(name, builder);
        });
        pendingBuilders.clear();
        builders1.forEach((name, builder) -> {
            // ...
            builder.entities().forEach(entityType -> {
                // ...
                entityBuilders.computeIfAbsent(entityType, it -> new ArrayList<>()).add(builder);
            });
            // ...
            builder.models().forEach(model -> {
                var modelClazz = ArmatureSerializers.getClass(model);
                if (modelClazz == null) {
                    ModLog.warn("Can't found model class '{}'", model);
                    return;
                }
                modelBuilders.computeIfAbsent(modelClazz, k -> new ArrayList<>()).add(builder);
            });
//            if (used == 0) {
//                defaultBuilders.add(builder);
//            }
            namedBuilders.put(name, builder);
        });
        version += 1;
    }

    public ArmatureTransformer getTransformer(EntityType<?> entityType, EntityProfile entityProfile, IEntityModel<?> entityModel) {
        var classes = new ArrayList<Class<?>>();
        var finalBuilders = new ArrayList<ArmatureTransformerBuilder>();
        if (entityModel != null) {
            var type = getType(entityModel);
            modelBuilders.forEach((clazz, builders) -> {
                if (clazz.isAssignableFrom(type)) {
                    for (var parent : classes) {
                        if (clazz.isAssignableFrom(parent)) {
                            return;
                        }
                    }
                    classes.add(clazz);
                    finalBuilders.addAll(builders);
                }
            });
        }
        if (entityType != null) {
            var resultBuilders = find(entityBuilders, entityType, IRegistryHolder::get);
            if (resultBuilders != null) {
                finalBuilders.addAll(resultBuilders);
            }
        }
        if (entityProfile != null) {
            for (var registryName : entityProfile.transformers()) {
                var builder = namedBuilders.get(registryName);
                if (builder != null) {
                    finalBuilders.add(builder);
                }
            }
        }
        if (!finalBuilders.isEmpty()) {
            var context = new ArmatureTransformerContext(entityType, entityModel);
            return finalBuilders.get(finalBuilders.size() - 1).build(context);
        }
        return null;
    }

    public int version() {
        return version;
    }

    private static <K, V, R> V find(Map<K, V> map, R req, Function<K, R> resolver) {
        for (var entry : map.entrySet()) {
            if (req.equals(resolver.apply(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static Class<?> getType(IEntityModel<?> entityModel) {
        if (entityModel instanceof LinkedModel<?> linkedModel) {
            return getType(linkedModel.parent());
        }
        return entityModel.getClass();
    }
}
