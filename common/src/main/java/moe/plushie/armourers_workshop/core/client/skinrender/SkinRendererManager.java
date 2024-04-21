package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRendererManager {

    private static int VERSION = 0;
    private static boolean IS_READY = false;

    private static final HashMap<EntityType<?>, BakedArmatureTransformer> FALLBACK_TRANSFORMERS = new HashMap<>();
    private static final HashMap<IEntityTypeProvider<?>, EntityProfile> ENTITIES = new HashMap<>();

    public static void init() {
        ModEntityProfiles.addListener(SkinRendererManager::unbind, SkinRendererManager::bind);
        SkinRendererManager.reload();
    }

    public static void reload() {
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            // call again later!!!
            RenderSystem.recordRenderCall(SkinRendererManager::reload);
            return;
        }
        RenderSystem.recordRenderCall(() -> _reload(entityRenderManager));
    }

    private static void _reload(EntityRenderDispatcher entityRenderManager) {

        for (EntityRenderer<? extends Player> renderer : entityRenderManager.playerRenderers.values()) {
            if (renderer instanceof LivingEntityRenderer<?, ?>) {
                setupRenderer(EntityType.PLAYER, (LivingEntityRenderer<?, ?>) renderer, true);
            }
        }

        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
                setupRenderer(entityType1, (LivingEntityRenderer<?, ?>) entityRenderer, true);
            }
        });

        // execute the pending tasks.
        IS_READY = false;
        FALLBACK_TRANSFORMERS.clear();
        ENTITIES.forEach(SkinRendererManager::_bind);
        VERSION += 1;
        IS_READY = true;
    }

    public static void unbind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        ModLog.debug("Detach Entity Renderer '{}'", entityType.getRegistryName());
        ENTITIES.remove(entityType);
        if (IS_READY) {
            // TODO: remove layer in the entity renderer.
        }
    }

    public static void bind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        ModLog.debug("Attach Entity Renderer '{}'", entityType.getRegistryName());
        ENTITIES.put(entityType, entityProfile);
        // try call once _bind to avoid the bind method being called after init.
        if (IS_READY) {
            _bind(entityType, entityProfile);
        }
    }

    private static void _bind(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
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
                } else {
                    setupFallbackRenderer(resolvedEntityType, renderer);
                }
            }
        });
    }

    private static <T extends LivingEntity, V extends EntityModel<T>> void setupRenderer(EntityType<?> entityType, LivingEntityRenderer<T, V> livingRenderer, boolean autoInject) {
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

    private static <T extends Entity> void setupFallbackRenderer(EntityType<?> entityType, EntityRenderer<T> renderer) {
        auto transformer = BakedArmatureTransformer.defaultBy(entityType, null, renderer);
        if (transformer != null) {
            FALLBACK_TRANSFORMERS.put(entityType, transformer);
        }
    }

    public static BakedArmatureTransformer getFallbackTransformer(EntityType<?> entityType) {
        return FALLBACK_TRANSFORMERS.get(entityType);
    }

    public static int getVersion() {
        return VERSION;
    }
}
