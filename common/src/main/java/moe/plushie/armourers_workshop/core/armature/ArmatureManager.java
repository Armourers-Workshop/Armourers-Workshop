package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class ArmatureManager {

    private final HashMap<ResourceLocation, ArmatureBuilder> pendingBuilders = new HashMap<>();

    private final ArrayList<ArmatureBuilder> defaultBuilders = new ArrayList<>();
    private final HashMap<IEntityType<?>, ArmatureBuilder> entityBuilders = new HashMap<>();

    private int version = 0;

    protected abstract ArmatureBuilder createBuilder(ResourceLocation name);

    public void clear() {
        pendingBuilders.clear();
    }

    public void append(IDataPackObject object, ResourceLocation location) {
        ArmatureBuilder builder = createBuilder(location);
        pendingBuilders.put(location, builder);
        builder.load(object);
    }

    public void freeze() {
        HashMap<ResourceLocation, ArmatureBuilder> builders1 = new HashMap<>();
        pendingBuilders.forEach((name, builder) -> {
            ArrayList<ArmatureBuilder> chain = new ArrayList<>();
            chain.add(builder);
            while (builder.getParent() != null) {
                ArmatureBuilder parent = pendingBuilders.get(builder.getParent());
                if (parent == null) {
                    break;
                }
                chain.add(0, parent);
                builder = parent;
            }
            if (chain.size() > 1) {
                ArmatureBuilder builder1 = createBuilder(name);
                builder1.load(chain);
                builder = builder1;
            }
            builders1.put(name, builder);
        });
        pendingBuilders.clear();
        builders1.forEach((name, builder) -> {
            Collection<IEntityType<?>> entities = builder.getEntities();
            if (entities.isEmpty()) {
                defaultBuilders.add(builder);
                return;
            }
            entities.forEach(entityType -> entityBuilders.put(entityType, builder));
        });
        version += 1;
    }

    public ITransformf[] getTransforms(EntityType<?> entityType, IModelHolder<?> model) {
        ArmatureBuilder builder = ObjectUtils.find(entityBuilders, entityType, IEntityType::get);
        if (builder == null && !defaultBuilders.isEmpty()) {
            builder = defaultBuilders.get(defaultBuilders.size() - 1);
        }
        if (builder != null) {
            return builder.build(model);
        }
        return null;
    }

    public int getVersion() {
        return version;
    }
}
