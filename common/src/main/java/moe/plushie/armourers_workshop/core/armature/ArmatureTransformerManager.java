package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ArmatureTransformerManager {

    private final HashMap<IResourceLocation, ArmatureTransformerBuilder> pendingBuilders = new HashMap<>();

    private final HashMap<IEntityTypeProvider<?>, ArrayList<ArmatureTransformerBuilder>> entityBuilders = new HashMap<>();
    private final HashMap<Class<?>, ArrayList<ArmatureTransformerBuilder>> modelBuilders = new HashMap<>();

    private int version = 0;

    protected abstract ArmatureTransformerBuilder createBuilder(IResourceLocation name);

    public void clear() {
        pendingBuilders.clear();
    }

    public void append(IDataPackObject object, IResourceLocation location) {
        var builder = createBuilder(location);
        pendingBuilders.put(location, builder);
        builder.load(object);
    }

    public void freeze() {
        var builders1 = new HashMap<IResourceLocation, ArmatureTransformerBuilder>();
        pendingBuilders.forEach((name, builder) -> {
            var chain = new ArrayList<ArmatureTransformerBuilder>();
            var nextBuilder = builder;
            while (nextBuilder.getParent() != null) {
                var parent = pendingBuilders.get(nextBuilder.getParent());
                if (parent == null) {
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
            builder.getEntities().forEach(entityType -> {
                // ...
                entityBuilders.computeIfAbsent(entityType, it -> new ArrayList<>()).add(builder);
            });
            // ...
            builder.getModels().forEach(model -> {
                var clazz = ArmatureSerializers.getClass(model);
                if (clazz != null) {
                    modelBuilders.computeIfAbsent(clazz, k -> new ArrayList<>()).add(builder);
                }
            });
//            if (used == 0) {
//                defaultBuilders.add(builder);
//            }
        });
        version += 1;
    }

    public ArmatureTransformer getTransformer(EntityType<?> entityType, IModel entityModel) {
        var classes = new ArrayList<Class<?>>();
        var finalBuilders = new ArrayList<ArmatureTransformerBuilder>();
        if (entityModel != null) {
            modelBuilders.forEach((clazz, builders) -> {
                if (clazz.isAssignableFrom(entityModel.getType())) {
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
            var resultBuilders = ObjectUtils.find(entityBuilders, entityType, IEntityTypeProvider::get);
            if (resultBuilders != null) {
                finalBuilders.addAll(resultBuilders);
            }
        }
        if (!finalBuilders.isEmpty()) {
            var context = new ArmatureTransformerContext(entityType, entityModel);
            return finalBuilders.get(finalBuilders.size() - 1).build(context);
        }
        return null;
    }

    public int getVersion() {
        return version;
    }
}
