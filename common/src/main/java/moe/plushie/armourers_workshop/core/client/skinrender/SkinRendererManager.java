package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRendererManager {

    private static final SkinRendererManager INSTANCE = new SkinRendererManager();

    private boolean isReady = false;

    private final HashMap<IEntityTypeProvider<?>, EntityProfile> entities = new HashMap<>();

    public static SkinRendererManager getInstance() {
        return INSTANCE;
    }

    public void reload() {
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            // call again later!!!
            RenderSystem.recordRenderCall(this::reload);
            return;
        }
        RenderSystem.recordRenderCall(() -> _reload(entityRenderManager));
    }

    private void _reload(EntityRenderDispatcher entityRenderManager) {
        SkinRendererManager skinRendererManager = getInstance();

        for (EntityRenderer<? extends Player> renderer : entityRenderManager.playerRenderers.values()) {
            if (renderer instanceof LivingEntityRenderer<?, ?>) {
                skinRendererManager.setupRenderer(EntityType.PLAYER, (LivingEntityRenderer<?, ?>) renderer, true);
            }
        }

        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
                skinRendererManager.setupRenderer(entityType1, (LivingEntityRenderer<?, ?>) entityRenderer, true);
            }
        });

        // execute the pending tasks.
        entities.forEach(this::_bind);
        isReady = true;
    }

    public void unbind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        ModLog.debug("Detach Entity Renderer '{}'", entityType.getRegistryName());
        entities.remove(entityType);
        if (isReady) {
            // TODO: remove layer in the entity renderer.
        }
    }

    public void bind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        ModLog.debug("Attach Entity Renderer '{}'", entityType.getRegistryName());
        entities.put(entityType, entityProfile);
        // try call once _bind to avoid the bind method being called after init.
        if (isReady) {
            _bind(entityType, entityProfile);
        }
    }

    private void _bind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        EntityType<?> resolvedEntityType = entityType.get();
        if (resolvedEntityType == null) {
            return;
        }
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            return;
        }
        // Add our own custom armor layer to the various player renderers.
        if (resolvedEntityType == EntityType.PLAYER) {
            for (EntityRenderer<? extends Player> renderer : entityRenderManager.playerRenderers.values()) {
                if (renderer instanceof LivingEntityRenderer<?, ?>) {
                    setupRenderer(resolvedEntityType, (LivingEntityRenderer<?, ?>) renderer, false);
                }
            }
        }
        // Add our own custom armor layer to everything that has an armor layer
        entityRenderManager.renderers.forEach((entityType1, renderer) -> {
            if (resolvedEntityType.equals(entityType1)) {
                if (renderer instanceof LivingEntityRenderer<?, ?>) {
                    setupRenderer(resolvedEntityType, (LivingEntityRenderer<?, ?>) renderer, false);
                }
            }
        });
    }

    private <T extends LivingEntity, V extends EntityModel<T>> void setupRenderer(EntityType<?> entityType, LivingEntityRenderer<T, V> livingRenderer, boolean autoInject) {
        RenderLayer<T, V> armorLayer = null;
        for (RenderLayer<T, V> layerRenderer : livingRenderer.layers) {
            if (layerRenderer instanceof HumanoidArmorLayer<?, ?, ?>) {
                armorLayer = layerRenderer;
            }
            if (layerRenderer instanceof SkinWardrobeLayer) {
                return; // ignore, only one.
            }
        }
        if (autoInject && armorLayer == null) {
            return;
        }
        auto transformer = BakedArmatureTransformer.defaultBy(entityType, livingRenderer.getModel(), livingRenderer);
        if (transformer != null) {
            livingRenderer.addLayer(new SkinWardrobeLayer<>(transformer, livingRenderer));
        }
    }


    public void willRender(Entity entity, Model entityModel, @Nullable EntityRenderer<?> entityRenderer, SkinRenderData renderData, Supplier<SkinRenderContext> context) {
        BakedArmatureTransformer transformer = BakedArmatureTransformer.defaultBy(entity, entityModel, entityRenderer);
        if (transformer != null) {
            SkinRenderContext context1 = context.get();
            transformer.prepare(entity, context1);
            context1.release();
        }
    }

    public void willRenderModel(Entity entity, Model entityModel, @Nullable EntityRenderer<?> entityRenderer, SkinRenderData renderData, Supplier<SkinRenderContext> context) {
        BakedArmatureTransformer transformer = BakedArmatureTransformer.defaultBy(entity, entityModel, entityRenderer);
        if (transformer != null) {
            SkinRenderContext context1 = context.get();
            transformer.activate(entity, context1);
            context1.release();
        }
    }

    public void didRender(Entity entity, Model entityModel, @Nullable EntityRenderer<?> entityRenderer, SkinRenderData renderData, Supplier<SkinRenderContext> context) {
        BakedArmatureTransformer transformer = BakedArmatureTransformer.defaultBy(entity, entityModel, entityRenderer);
        if (transformer != null) {
            SkinRenderContext context1 = context.get();
            transformer.deactivate(entity, context1);
            context1.release();
        }
    }
}
