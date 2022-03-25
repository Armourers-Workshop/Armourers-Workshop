package moe.plushie.armourers_workshop.core.render.skin;

import com.google.common.collect.Maps;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import moe.plushie.armourers_workshop.core.render.layer.DelegateBipedArmorLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SkinRendererManager {

    private static final SkinRendererManager INSTANCE = new SkinRendererManager();
    private final Map<EntityType<?>, SkinRenderer<?, ?>> renderers = Maps.newHashMap();

    private EntityType<?> lastEntityType;
    private SkinRenderer<?, ?> lastRenderer;

    public static void init() {
//        DelegateBipedArmorLayer
        SkinRendererManager skinRendererManager = getInstance();
        EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        for (PlayerRenderer playerRenderer : entityRenderManager.getSkinMap().values()) {
            skinRendererManager.setupArmorLayerRenderer(playerRenderer);
        }
        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityRenderer instanceof LivingRenderer<?, ?>) {
                skinRendererManager.setupArmorLayerRenderer((LivingRenderer<?, ?>) entityRenderer);
            }
        });
    }

    public static SkinRendererManager getInstance() {
        return INSTANCE;
    }

    public <T extends Entity, M extends Model> void register(EntityType<T> entityType, Function<EntityProfile, SkinRenderer<T, M>> rendererBuilder) {
        EntityProfile entityProfile = EntityProfiles.getProfile(entityType);
        EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityProfile == null) {
            return;
        }
        SkinRenderer<T, M> skinRenderer = rendererBuilder.apply(entityProfile);
        skinRenderer.initTransformers();
        // Add our own custom armor layer to the various player renderers.
        if (entityType == EntityType.PLAYER) {
            for (PlayerRenderer playerRenderer : entityRenderManager.getSkinMap().values()) {
                setupRenderer(EntityType.PLAYER, playerRenderer, skinRenderer);
            }
        }
        // Add our own custom armor layer to everything that has an armor layer
        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityType.equals(entityType1)) {
                setupRenderer(entityType, entityRenderer, skinRenderer);
            }
        });
        this.renderers.put(entityType, skinRenderer);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Entity, M extends Model> SkinRenderer<T, M> getRenderer(@Nullable T entity) {
        if (entity == null) {
            return null;
        }
        EntityType<?> entityType = entity.getType();
        if (lastEntityType != null && lastEntityType.equals(entityType)) {
            return (SkinRenderer<T, M>) lastRenderer;
        }
        SkinRenderer<?, ?> renderer = renderers.get(entityType);
        if (renderer != null) {
            lastEntityType = entityType;
            lastRenderer = renderer;
            return (SkinRenderer<T, M>) renderer;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Entity, M extends Model> SkinRenderer<T, M> getRenderer(@Nullable EntityType<?> entityType) {
        if (entityType == null) {
            return null;
        }
        if (lastEntityType != null && lastEntityType.equals(entityType)) {
            return (SkinRenderer<T, M>) lastRenderer;
        }
        SkinRenderer<?, ?> renderer = renderers.get(entityType);
        if (renderer != null) {
            lastEntityType = entityType;
            lastRenderer = renderer;
            return (SkinRenderer<T, M>) renderer;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity, M extends Model> void setupRenderer(EntityType<?> entityType, EntityRenderer<?> entityRenderer, SkinRenderer<T, M> skinRenderer) {
        skinRenderer.init((EntityRenderer<T>) entityRenderer);
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void setupArmorLayerRenderer(LivingRenderer<T, M> livingRenderer) {
        List<LayerRenderer<T, M>> layers = livingRenderer.layers;
        for (int i = 0; i < layers.size(); ++i) {
            LayerRenderer<T, M> layerRenderer = layers.get(i);
            if (layerRenderer instanceof BipedArmorLayer<?, ?, ?>) {
                layerRenderer = new DelegateBipedArmorLayer(livingRenderer, (BipedArmorLayer) layerRenderer);
                livingRenderer.layers.set(i, layerRenderer);
            }
        }
    }
}
