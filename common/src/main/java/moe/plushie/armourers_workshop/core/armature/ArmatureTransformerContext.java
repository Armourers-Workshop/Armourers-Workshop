package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ArmatureTransformerContext {

    private final EntityType<?> entityType;
    private IModel entityModel;
    private EntityRenderer<?> entityRenderer;

    private final ArrayList<Consumer<IModel>> entityModelListeners = new ArrayList<>();
    private final ArrayList<Consumer<EntityRenderer<?>>> entityRendererListeners = new ArrayList<>();

    public ArmatureTransformerContext(EntityType<?> entityType, IModel entityModel) {
        this.entityType = entityType;
        this.entityModel = entityModel;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityModel(IModel entityModel) {
        this.entityModel = entityModel;
        this.entityModelListeners.forEach(it -> it.accept(entityModel));
    }

    public void setEntityModel0(IModel entityModel) {
        this.entityModel = entityModel;
    }

    public IModel getEntityModel() {
        return entityModel;
    }

    public void setEntityRenderer(EntityRenderer<?> entityRenderer) {
        this.entityRenderer = entityRenderer;
        this.entityRendererListeners.forEach(it -> it.accept(entityRenderer));
    }

    public void setEntityRenderer0(EntityRenderer<?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public EntityRenderer<?> getEntityRenderer() {
        return entityRenderer;
    }

    public void addEntityModelListener(Consumer<IModel> callback) {
        entityModelListeners.add(callback);
        if (entityModel != null) {
            callback.accept(entityModel);
        }
    }

    public void addEntityRendererListener(Consumer<EntityRenderer<?>> callback) {
        entityRendererListeners.add(callback);
        if (entityRenderer != null) {
            callback.accept(entityRenderer);
        }
    }
}
