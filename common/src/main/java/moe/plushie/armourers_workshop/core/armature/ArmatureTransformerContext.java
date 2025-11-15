package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ArmatureTransformerContext {

    private final EntityType<?> entityType;
    private IEntityModel<?> entityModel;
    private IEntityRenderer<?, ?> entityRenderer;

    private final ArrayList<Consumer<IEntityModel<?>>> entityModelListeners = new ArrayList<>();
    private final ArrayList<Consumer<IEntityRenderer<?, ?>>> entityRendererListeners = new ArrayList<>();

    public ArmatureTransformerContext(EntityType<?> entityType, IEntityModel<?> entityModel) {
        this.entityType = entityType;
        this.entityModel = entityModel;
    }

    public EntityType<?> entityType() {
        return entityType;
    }

    public void setEntityModel(IEntityModel<?> entityModel) {
        this.entityModel = entityModel;
        this.entityModelListeners.forEach(it -> it.accept(entityModel));
    }

    public void setEntityModel0(IEntityModel<?> entityModel) {
        this.entityModel = entityModel;
    }

    public IEntityModel<?> entityModel() {
        return entityModel;
    }

    public void setEntityRenderer(IEntityRenderer<?, ?> entityRenderer) {
        this.entityRenderer = entityRenderer;
        this.entityRendererListeners.forEach(it -> it.accept(entityRenderer));
    }

    public void setEntityRenderer0(IEntityRenderer<?, ?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public IEntityRenderer<?, ?> entityRenderer() {
        return entityRenderer;
    }

    public void addEntityModelListener(Consumer<IEntityModel<?>> callback) {
        entityModelListeners.add(callback);
        if (entityModel != null) {
            callback.accept(entityModel);
        }
    }

    public void addEntityRendererListener(Consumer<IEntityRenderer<?, ?>> callback) {
        entityRendererListeners.add(callback);
        if (entityRenderer != null) {
            callback.accept(entityRenderer);
        }
    }
}
