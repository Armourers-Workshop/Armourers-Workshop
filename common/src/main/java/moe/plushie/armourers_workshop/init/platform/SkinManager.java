package moe.plushie.armourers_workshop.init.platform;


import moe.plushie.armourers_workshop.api.client.ISkinRendererProvider;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.client.skinrender.*;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class SkinManager {

    public static void init() {
        SkinRendererManager manager = SkinRendererManager.getInstance();
        adapt(manager::registerRenderer);
        ModEntityProfiles.forEach(manager::bind);
        manager.init();
    }

    protected static void adapt(Consumer<Builder> manager) {
        // using special skin renderer of the arrow.
        manager.accept(Builder.of(ArrowSkinRenderer::new).whenRenderer(ArrowRenderer.class));
        manager.accept(Builder.of(TridentSkinRenderer::new).whenRenderer(ThrownTridentRenderer.class));

        manager.accept(Builder.of(IllagerSkinRenderer::new).whenModel(IllagerModel.class));
        manager.accept(Builder.of(ZombieVillagerSkinRenderer::new).whenModel(ZombieVillagerModel.class));
        manager.accept(Builder.of(VillagerSkinRenderer::new).whenModel(VillagerModel.class));
        manager.accept(Builder.of(IronGolemSkinRenderer::new).whenModel(IronGolemModel.class));

        manager.accept(Builder.of(FirstPersonSkinRenderer::new).whenModel(FirstPersonPlayerModel.class));
        manager.accept(Builder.of(PlayerSkinRenderer::new).whenModel(PlayerModel.class));
        manager.accept(Builder.of(BipedSkinRenderer::new).whenModel(HumanoidModel.class));

        manager.accept(Builder.of(SlimeSkinRenderer::new).whenModel(SlimeModel.class));
        manager.accept(Builder.of(GhastSkinRenderer::new).whenModel(GhastModel.class));
        manager.accept(Builder.of(ChickenSkinRenderer::new).whenModel(ChickenModel.class));
        manager.accept(Builder.of(CreeperSkinRenderer::new).whenModel(CreeperModel.class));
    }

    protected static class Builder implements ISkinRendererProvider<SkinRenderer<?, ?>> {

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
        @Override
        public SkinRenderer<?, ?> create(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile) {
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
