package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.client.skinrender.*;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.MobLayerFixPlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.SlimeOuterFixPlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.VillagerLayerFixPlugin;
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
        ModEntityProfiles.addListener(manager::unbind, manager::bind);
        //
        manager.registerPlugin(SlimeSkinRenderer.class, new SlimeOuterFixPlugin<>());
        manager.registerPlugin(VillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(ZombieVillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(BipedSkinRenderer.class, new MobLayerFixPlugin<>());
    }

    protected static void adapt(Consumer<Builder> manager) {
        // using special skin renderer of the arrow.
        manager.accept(Builder.of(ArrowSkinRenderer::new).renderer(ArrowRenderer.class));
        manager.accept(Builder.of(TridentSkinRenderer::new).renderer(ThrownTridentRenderer.class));

        manager.accept(Builder.of(IllagerSkinRenderer::new).model(IllagerModel.class));
        manager.accept(Builder.of(ZombieVillagerSkinRenderer::new).model(ZombieVillagerModel.class));
        manager.accept(Builder.of(VillagerSkinRenderer::new).model(VillagerModel.class));
        manager.accept(Builder.of(IronGolemSkinRenderer::new).model(IronGolemModel.class));

        manager.accept(Builder.of(FirstPersonSkinRenderer::new).model(FirstPersonPlayerModel.class));
        manager.accept(Builder.of(PlayerSkinRenderer::new).model(PlayerModel.class));
        manager.accept(Builder.of(BipedSkinRenderer::new).model(HumanoidModel.class));

        manager.accept(Builder.of(SlimeSkinRenderer::new).model(SlimeModel.class));
        manager.accept(Builder.of(GhastSkinRenderer::new).model(GhastModel.class));
        manager.accept(Builder.of(ChickenSkinRenderer::new).model(ChickenModel.class));
        manager.accept(Builder.of(CreeperSkinRenderer::new).model(CreeperModel.class));
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
