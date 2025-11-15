package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.api.event.client.AddRendererLayerEvent;
import moe.plushie.armourers_workshop.api.event.client.RemoveRendererLayerEvent;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.render.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

public class EntityRendererContext {

    private int version = 0;

    private EntityType<?> entityType;
    private EntityProfile entityProfile;

    private final IEntityRenderer<?, ?> entityRenderer;
    private final IdentityHashMap<IEntityModel<?>, BakedArmatureTransformer> cachedTransformers = new IdentityHashMap<>();

    public EntityRendererContext(IEntityRenderer<?, ?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public static EntityRendererContext of(IEntityRenderer<?, ?> entityRenderer) {
        return DataContainer.of(entityRenderer, EntityRendererContext::new);
    }

    @Nullable
    public BakedArmatureTransformer createTransformer(@Nullable IEntityModel<?> entityModel, ArmatureTransformerManager transformerManager) {
        // when entity type and entity profile not provide, this means entity renderer not support yet.
        if (entityType == null || entityProfile == null) {
            return null;
        }
        var transformer = transformerManager.getTransformer(entityType, entityProfile, entityModel);
        if (transformer != null) {
            return BakedArmatureTransformer.create(transformer, entityRenderer);
        }
        return null;
    }

    @Nullable
    public BakedArmatureTransformer getTransformer(@Nullable IEntityModel<?> entityModel) {
        // when entity type and entity profile not provide, this means entity renderer not support yet.
        if (entityType == null || entityProfile == null) {
            return null;
        }
        // when the caller does not provide the entity model we need to query it from entity render.
        if (entityModel == null) {
            entityModel = entityModel();
        }
        // in the normal, the entityRenderer only have a model type,
        // but some mods(Custom NPC) generate dynamically models,
        // so we need to be compatible with that
        return cachedTransformers.computeIfAbsent(entityModel, entityModel1 -> {
            // ..
            return createTransformer(entityModel1, SkinRendererManager.DEFAULT);
        });
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public EntityType<?> entityType() {
        return entityType;
    }

    public void setEntityProfile(EntityProfile entityProfile) {
        if (Objects.equals(this.entityProfile, entityProfile)) {
            return;
        }
        var oldValue = this.entityProfile;
        this.cachedTransformers.clear();
        this.entityProfile = entityProfile;
        this.version += 1;
        // add or remove our own custom armor layer.
        if (entityRenderer instanceof ILivingEntityRenderer<?, ?, ?> livingEntityRenderer) {
            if (oldValue == null && entityProfile != null) {
                addLayer(Objects.unsafeCast(livingEntityRenderer));
            }
            if (oldValue != null && entityProfile == null) {
                removeLayer(Objects.unsafeCast(livingEntityRenderer));
            }
        }
    }

    public EntityProfile entityProfile() {
        return entityProfile;
    }

    public IEntityModel<?> entityModel() {
        if (entityRenderer instanceof ILivingEntityRenderer<?, ?, ?> livingRenderer) {
            return livingRenderer.abi$getModel();
        }
        return null;
    }

    public int version() {
        return version;
    }

    private <T extends LivingEntity, S extends LivingEntityRenderState, M extends IEntityModel<S>> void addLayer(ILivingEntityRenderer<T, S, M> livingRenderer) {
        removeLayer(livingRenderer);
        var transformer = getTransformer(null);
        if (transformer != null) {
            var layer = new SkinWardrobeLayer<>(transformer, livingRenderer);
            livingRenderer.abi$getLayers().add(0, layer);
            didAddLayer(layer, livingRenderer);
        }
    }

    private <T extends LivingEntity, S extends LivingEntityRenderState, M extends IEntityModel<S>> void removeLayer(ILivingEntityRenderer<T, S, M> livingRenderer) {
        var iterator = livingRenderer.abi$getLayers().iterator();
        while (iterator.hasNext()) {
            var layer = iterator.next();
            if (layer instanceof SkinWardrobeLayer) {
                iterator.remove();
                didRemoveLayer(layer, livingRenderer);
            }
        }
    }

    private void didAddLayer(Object layer, Object renderer) {
        EventManager.post(AddRendererLayerEvent.class, new AddRendererLayerEvent() {
            @Override
            public Object getLayer() {
                return layer;
            }

            @Override
            public Object getRenderer() {
                return renderer;
            }
        });
    }

    private void didRemoveLayer(Object layer, Object renderer) {
        EventManager.post(RemoveRendererLayerEvent.class, new RemoveRendererLayerEvent() {
            @Override
            public Object getLayer() {
                return layer;
            }

            @Override
            public Object getRenderer() {
                return renderer;
            }
        });
    }
}
