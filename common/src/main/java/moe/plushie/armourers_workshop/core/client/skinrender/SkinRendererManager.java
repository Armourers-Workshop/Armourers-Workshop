package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.ISkinRendererProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class SkinRendererManager {

    private static final SkinRendererManager INSTANCE = new SkinRendererManager();

    private final ArrayList<ISkinRendererProvider<SkinRenderer<?, ?>>> builders = new ArrayList<>();
    private ArrayList<Runnable> pendingTasks = new ArrayList<>();

    public static SkinRendererManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            RenderSystem.recordRenderCall(this::init);
            return;
        }
        SkinRendererManager skinRendererManager = getInstance();

        for (PlayerRenderer playerRenderer : entityRenderManager.playerRenderers.values()) {
            skinRendererManager.setupRenderer(EntityType.PLAYER, playerRenderer, true);
        }

        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
                skinRendererManager.setupRenderer(entityType1, (LivingEntityRenderer<?, ?>) entityRenderer, true);
            }
        });

        // execute the pending tasks.
        ArrayList<Runnable> tasks = skinRendererManager.pendingTasks;
        skinRendererManager.pendingTasks = null;
        tasks.forEach(Runnable::run);
    }

    public void bind(EntityType<?> entityType, EntityProfile entityProfile) {
        // if the manager not ready, register again later.
        if (pendingTasks != null) {
            pendingTasks.add(() -> bind(entityType, entityProfile));
            return;
        }
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            return;
        }
        // Add our own custom armor layer to the various player renderers.
        if (entityType == EntityType.PLAYER) {
            for (PlayerRenderer playerRenderer : entityRenderManager.playerRenderers.values()) {
                setupRenderer(entityType, playerRenderer, false);
            }
        }
        // Add our own custom armor layer to everything that has an armor layer
        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (entityType.equals(entityType1)) {
                if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
                    setupRenderer(entityType, (LivingEntityRenderer<?, ?>) entityRenderer, false);
                }
            }
        });
    }

    public void registerRenderer(ISkinRendererProvider<SkinRenderer<?, ?>> builder) {
        builders.add(builder);
    }

    @Nullable
    public <T extends Entity, M extends Model> SkinRenderer<T, M> getRenderer(@Nullable T entity, @Nullable Model entityModel, @Nullable EntityRenderer<?> entityRenderer) {
        if (entity == null) {
            return null;
        }
        EntityType<?> entityType = entity.getType();
        // when the caller does not provide the entity renderer we need to query it from managers.
        if (entityRenderer == null) {
            entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        }
        // when the caller does not provide the entity model we need to query it from entity render.
        if (entityModel == null) {
            entityModel = getModel(entityRenderer);
        }
        return getRenderer(entityType, entityModel, entityRenderer);
    }

    @Nullable
    protected <T extends Entity, M extends Model> SkinRenderer<T, M> getRenderer(EntityType<?> entityType, Model entityModel, EntityRenderer<?> entityRenderer) {
        // in the normal, the entityRenderer only one model type,
        // but some mods generate dynamically models,
        // so we need to be compatible with that
        ISkinDataProvider dataProvider = (ISkinDataProvider) entityRenderer;
        HashMap<Object, SkinRenderer<T, M>> skinRenderers = dataProvider.getSkinData();
        if (skinRenderers == null) {
            skinRenderers = new HashMap<>();
            dataProvider.setSkinData(skinRenderers);
        }
        Class<?> key = getModelClass(entityModel);
        SkinRenderer<T, M> skinRenderer = skinRenderers.get(key);
        if (skinRenderer != null) {
            return skinRenderer;
        }
        skinRenderer = createRenderer(entityType, entityRenderer, entityModel);
        if (skinRenderer != null) {
            skinRenderers.put(key, skinRenderer);
        }
        return skinRenderer;
    }


    @Nullable
    protected <T extends Entity, M extends Model> SkinRenderer<T, M> createRenderer(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel) {
        EntityProfile entityProfile = ModEntityProfiles.getProfile(entityType);
        for (ISkinRendererProvider<SkinRenderer<?, ?>> builder : builders) {
            SkinRenderer<?, ?> skinRenderer = builder.create(entityType, entityRenderer, entityModel, entityProfile);
            if (skinRenderer != null) {
                return ObjectUtils.unsafeCast(skinRenderer);
            }
        }
        return null;
    }

    protected Class<?> getModelClass(Model model) {
        if (model != null) {
            return model.getClass();
        }
        return Model.class;
    }

    protected EntityModel<?> getModel(EntityRenderer<?> entityRenderer) {
        if (entityRenderer instanceof RenderLayerParent) {
            return ((RenderLayerParent<?, ?>) entityRenderer).getModel();
        }
        return null;
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void setupRenderer(EntityType<?> entityType, LivingEntityRenderer<T, M> livingRenderer, boolean autoInject) {
        RenderLayer<T, M> armorLayer = null;
        for (RenderLayer<T, M> layerRenderer : livingRenderer.layers) {
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
        SkinRenderer<T, M> skinRenderer = getRenderer(entityType, livingRenderer.getModel(), livingRenderer);
        livingRenderer.addLayer(new SkinWardrobeLayer<>(skinRenderer, livingRenderer));
    }
}
