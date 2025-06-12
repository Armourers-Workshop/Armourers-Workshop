package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.event.client.AddRendererLayerEvent;
import moe.plushie.armourers_workshop.api.event.client.RemoveRendererLayerEvent;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class EntityRendererContext {

    private int version = 0;

    private EntityType<?> entityType;
    private EntityProfile entityProfile;

    private final EntityRenderer<?> entityRenderer;
    private final HashMap<EntityModel<?>, BakedArmatureTransformer> cachedTransformers = new HashMap<>();

    public EntityRendererContext(EntityRenderer<?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public static EntityRendererContext of(EntityRenderer<?> entityRenderer) {
        return DataContainer.of(entityRenderer, EntityRendererContext::new);
    }

    @Nullable
    public BakedArmatureTransformer createTransformer(@Nullable IModel entityModel, ArmatureTransformerManager transformerManager) {
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
    public BakedArmatureTransformer getTransformer(@Nullable EntityModel<?> entityModel) {
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
            // if it can't transform this, it means we do not support this renderer.
            var model = AbstractModelHolder.ofNullable(entityModel1);
            return createTransformer(model, SkinRendererManager.DEFAULT);
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
        if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
            if (oldValue == null && entityProfile != null) {
                addLayer(livingEntityRenderer);
            }
            if (oldValue != null && entityProfile == null) {
                removeLayer(livingEntityRenderer);
            }
        }
    }

    public EntityProfile entityProfile() {
        return entityProfile;
    }

    public EntityModel<?> entityModel() {
        if (entityRenderer instanceof RenderLayerParent<?, ?> modelProvider) {
            return modelProvider.getModel();
        }
        return null;
    }

    public int version() {
        return version;
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void addLayer(LivingEntityRenderer<T, V> livingRenderer) {
        removeLayer(livingRenderer);
        var transformer = getTransformer(null);
        if (transformer != null) {
            var layer = new SkinWardrobeLayer<>(transformer, livingRenderer);
            livingRenderer.layers.add(0, layer);
            didAddLayer(layer, livingRenderer);
        }
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void removeLayer(LivingEntityRenderer<T, V> livingRenderer) {
        var iterator = livingRenderer.layers.iterator();
        while (iterator.hasNext()) {
            var layer = iterator.next();
            if (layer instanceof SkinWardrobeLayer<?, ?, ?>) {
                iterator.remove();
                didRemoveLayer(layer, livingRenderer);
            }
        }
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void didAddLayer(RenderLayer<T, V> layer, LivingEntityRenderer<T, V> renderer) {
        EventManager.post(AddRendererLayerEvent.class, new AddRendererLayerEvent<T, V>() {
            @Override
            public RenderLayer<T, V> getLayer() {
                return layer;
            }

            @Override
            public LivingEntityRenderer<T, V> getRenderer() {
                return renderer;
            }
        });
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void didRemoveLayer(RenderLayer<T, V> layer, LivingEntityRenderer<T, V> renderer) {
        EventManager.post(RemoveRendererLayerEvent.class, new RemoveRendererLayerEvent<T, V>() {
            @Override
            public RenderLayer<T, V> getLayer() {
                return layer;
            }

            @Override
            public LivingEntityRenderer<T, V> getRenderer() {
                return renderer;
            }
        });
    }
}
