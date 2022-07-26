package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.client.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class SkinRendererManager {

    private static final SkinRendererManager INSTANCE = new SkinRendererManager();

    private final ArrayList<Builder> builders = new ArrayList<>();
    private ArrayList<Runnable> pendingTasks = new ArrayList<>();

    public static SkinRendererManager getInstance() {
        return INSTANCE;
    }

    public static void init() {
        EntityRenderDispatcher entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            RenderSystem.recordRenderCall(SkinRendererManager::init);
            return;
        }
        SkinRendererManager skinRendererManager = getInstance();
        skinRendererManager.registerRendererBuilders();
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


    public <T extends Entity, M extends Model> void register(EntityType<T> entityType, EntityProfile entityProfile) {
        // if the manager not ready, register again later.
        if (pendingTasks != null) {
            pendingTasks.add(() -> register(entityType, entityProfile));
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
        for (Builder builder : builders) {
            SkinRenderer<?, ?> skinRenderer = builder.build(entityType, entityRenderer, entityModel, entityProfile);
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

    protected void registerRendererBuilders() {
        // using special skin renderer of the arrow.
        builders.add(Builder.of(ArrowSkinRenderer::new).whenRenderer(ArrowRenderer.class));
        builders.add(Builder.of(TridentSkinRenderer::new).whenRenderer(ThrownTridentRenderer.class));

        builders.add(Builder.of(IllagerSkinRenderer::new).whenModel(IllagerModel.class));
        builders.add(Builder.of(ZombieVillagerSkinRenderer::new).whenModel(ZombieVillagerModel.class));
        builders.add(Builder.of(VillagerSkinRenderer::new).whenModel(VillagerModel.class));

        builders.add(Builder.of(FirstPersonSkinRenderer::new).whenModel(FirstPersonPlayerModel.class));
        builders.add(Builder.of(PlayerSkinRenderer::new).whenModel(PlayerModel.class));
        builders.add(Builder.of(BipedSkinRenderer::new).whenModel(HumanoidModel.class));

        builders.add(Builder.of(SlimeSkinRenderer::new).whenModel(SlimeModel.class));
        builders.add(Builder.of(GhastSkinRenderer::new).whenModel(GhastModel.class));
    }

    protected static class Builder {

        Class<?> modelClass;
        Class<?> rendererClass;
        Function<EntityProfile, SkinRenderer<?, ?>> factory;

        public static Builder of(Function<EntityProfile, SkinRenderer<?, ?>> factory) {
            Builder builder = new Builder();
            builder.factory = factory;
            return builder;
        }

        public <T> Builder whenModel(Class<T> modelClass) {
            this.modelClass = modelClass;
            return this;
        }

        public <T> Builder whenRenderer(Class<T> rendererClass) {
            this.rendererClass = rendererClass;
            return this;
        }

        @Nullable
        public SkinRenderer<?, ?> build(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile) {
            // when specify the type of the model, we need to check it.
            if (this.modelClass != null && !this.modelClass.isInstance(entityModel)) {
                return null;
            }
            // when specify the type of the renderer, we need to check it.
            if (this.rendererClass != null && !this.rendererClass.isInstance(entityRenderer)) {
                return null;
            }
            SkinRenderer<?, ?> skinRenderer = this.factory.apply(entityProfile);
            skinRenderer.initTransformers();
            skinRenderer.init(ObjectUtils.unsafeCast(entityRenderer));
            return skinRenderer;
        }
    }
}
