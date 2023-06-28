package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.client.skinrender.ArrowSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.BipedSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.ChickenSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.CreeperSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.FirstPersonSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.GhastSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.IllagerSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.IronGolemSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.PlayerSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.client.skinrender.SlimeSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.TridentSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.VillagerSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.ZombieVillagerSkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.MobLayerFixPlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.SlimeOuterFixPlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.VillagerLayerFixPlugin;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class SkinRendererRegistries {

    public static void init() {
        SkinRendererManager manager = SkinRendererManager.getInstance();
        adapt(manager::registerRenderer);
        ModEntityProfiles.addListener(manager::unbind, manager::bind);
        //
        manager.registerPlugin(SlimeSkinRenderer.class, new SlimeOuterFixPlugin<>());
        manager.registerPlugin(VillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(ZombieVillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(BipedSkinRenderer.class, new MobLayerFixPlugin<>());
    }

    protected static void adapt(Consumer<Builder> handler) {
        // using special skin renderer of the arrow.
        handler.accept(Builder.of(ArrowSkinRenderer::new).renderer(ArrowRenderer.class));
        handler.accept(Builder.of(TridentSkinRenderer::new).renderer(ThrownTridentRenderer.class));

        handler.accept(Builder.of(IllagerSkinRenderer::new).model(IllagerModel.class));
        handler.accept(Builder.of(ZombieVillagerSkinRenderer::new).model(ZombieVillagerModel.class));
        handler.accept(Builder.of(VillagerSkinRenderer::new).model(VillagerModel.class));
        handler.accept(Builder.of(IronGolemSkinRenderer::new).model(IronGolemModel.class));

        handler.accept(Builder.of(FirstPersonSkinRenderer::new).model(FirstPersonPlayerModel.class));
        handler.accept(Builder.of(PlayerSkinRenderer::new).model(PlayerModel.class));
        handler.accept(Builder.of(BipedSkinRenderer::new).model(HumanoidModel.class));

        handler.accept(Builder.of(SlimeSkinRenderer::new).model(SlimeModel.class));
        handler.accept(Builder.of(GhastSkinRenderer::new).model(GhastModel.class));
        handler.accept(Builder.of(ChickenSkinRenderer::new).model(ChickenModel.class));
        handler.accept(Builder.of(CreeperSkinRenderer::new).model(CreeperModel.class));
    }

    protected static class Builder implements SkinRenderer.Factory<SkinRenderer<?, ?, ?>> {

        Class<?> modelClass;
        Class<?> rendererClass;
        Function<EntityProfile, SkinRenderer<?, ?, ?>> factory;

        public static Builder of(Function<EntityProfile, SkinRenderer<?, ?, ?>> factory) {
            Builder builder = new Builder();
            builder.factory = factory;
            return builder;
        }

        public <T> Builder model(Class<T> modelClass) {
            this.modelClass = modelClass;
            return this;
        }

        public <T> Builder renderer(Class<T> rendererClass) {
            this.rendererClass = rendererClass;
            return this;
        }

        @Nullable
        @Override
        public SkinRenderer<?, ?, ?> create(EntityType<?> entityType, EntityRenderer<?> entityRenderer, Model entityModel, EntityProfile entityProfile) {
            // when specify the type of the model, we need to check it.
            if (this.modelClass != null && !this.modelClass.isInstance(entityModel)) {
                return null;
            }
            // when specify the type of the renderer, we need to check it.
            if (this.rendererClass != null && !this.rendererClass.isInstance(entityRenderer)) {
                return null;
            }
            SkinRenderer<?, ?, ?> skinRenderer = this.factory.apply(entityProfile);
            skinRenderer.initTransformers();
            skinRenderer.init(ObjectUtils.unsafeCast(entityRenderer));
            return skinRenderer;
        }
    }
}
