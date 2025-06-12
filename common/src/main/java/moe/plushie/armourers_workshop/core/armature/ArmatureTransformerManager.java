package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ArmatureTransformerManager {

    private final HashMap<IResourceLocation, ArmatureTransformerBuilder> pendingBuilders = new HashMap<>();

    private final HashMap<IResourceLocation, ArmatureTransformerBuilder> namedBuilders = new HashMap<>();
    private final HashMap<IEntityTypeProvider<?>, ArrayList<ArmatureTransformerBuilder>> entityBuilders = new HashMap<>();
    private final HashMap<Class<?>, ArrayList<ArmatureTransformerBuilder>> modelBuilders = new HashMap<>();

    private int version = 0;

    protected abstract ArmatureTransformerBuilder createBuilder(IResourceLocation name);

    public void clear() {
        pendingBuilders.clear();
    }

    public void append(IResourceLocation registryName, IODataObject object) {
        var builder = createBuilder(registryName);
        pendingBuilders.put(registryName, builder);
        builder.load(object);
    }

    public void freeze() {
        var builders1 = new HashMap<IResourceLocation, ArmatureTransformerBuilder>();
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

    public ArmatureTransformer getTransformer(EntityType<?> entityType, EntityProfile entityProfile, IModel entityModel) {
        var classes = new ArrayList<Class<?>>();
        var finalBuilders = new ArrayList<ArmatureTransformerBuilder>();
        if (entityModel != null) {
            modelBuilders.forEach((clazz, builders) -> {
                if (clazz.isAssignableFrom(entityModel.type())) {
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
            var resultBuilders = find(entityBuilders, entityType, IEntityTypeProvider::get);
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

    public static <K, V, R> V find(Map<K, V> map, R req, Function<K, R> resolver) {
        for (var entry : map.entrySet()) {
            if (req.equals(resolver.apply(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return null;
    }
}
