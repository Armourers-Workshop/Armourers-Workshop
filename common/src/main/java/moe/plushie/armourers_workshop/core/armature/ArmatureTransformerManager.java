package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ArmatureTransformerManager {

    private final HashMap<ResourceLocation, ArmatureTransformerBuilder> pendingBuilders = new HashMap<>();

    private final HashMap<Class<?>, ArrayList<ArmatureTransformerBuilder>> modelBuilders = new HashMap<>();

    private int version = 0;

    protected abstract ArmatureTransformerBuilder createBuilder(ResourceLocation name);

    public void clear() {
        pendingBuilders.clear();
    }

    public void append(IDataPackObject object, ResourceLocation location) {
        ArmatureTransformerBuilder builder = createBuilder(location);
        pendingBuilders.put(location, builder);
        builder.load(object);
    }

    public void freeze() {
        HashMap<ResourceLocation, ArmatureTransformerBuilder> builders1 = new HashMap<>();
        pendingBuilders.forEach((name, builder) -> {
            ArrayList<ArmatureTransformerBuilder> chain = new ArrayList<>();
            ArmatureTransformerBuilder nextBuilder = builder;
            while (nextBuilder.getParent() != null) {
                ArmatureTransformerBuilder parent = pendingBuilders.get(nextBuilder.getParent());
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
            int used = 0;
//            Collection<IEntityTypeProvider<?>> entities = builder.getEntities();
//            if (!entities.isEmpty()) {
//                entities.forEach(entityType -> entityBuilders.put(entityType, builder));
//                used += 1;
//            }
            Collection<ResourceLocation> models = builder.getModels();
            if (!models.isEmpty()) {
                models.forEach(model -> {
                    Class<?> clazz = SkinRendererManager2.NAMED_CLASSES.get(model);
                    if (clazz != null) {
                        modelBuilders.computeIfAbsent(clazz, k -> new ArrayList<>()).add(builder);
                    }
                });
//                used += 1;
            }
//            if (used == 0) {
//                defaultBuilders.add(builder);
//            }
        });
        version += 1;
    }

    public ArmatureTransformer getTransformer(EntityType<?> entityType, IModel entityModel) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ArrayList<ArmatureTransformerBuilder> finalBuilders = new ArrayList<>();
        modelBuilders.forEach((clazz, builders) -> {
            if (clazz.isAssignableFrom(entityModel.getType())) {
                for (Class<?> parent : classes) {
                    if (clazz.isAssignableFrom(parent)) {
                        return;
                    }
                }
                classes.add(clazz);
                finalBuilders.addAll(builders);
            }
        });
        if (finalBuilders.size() >= 1) {
            return finalBuilders.get(finalBuilders.size() - 1).build(entityModel);
        }
        return null;
    }

    public int getVersion() {
        return version;
    }
}
