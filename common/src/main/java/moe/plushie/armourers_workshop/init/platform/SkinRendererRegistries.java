package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.compatibility.client.model.AbstractSkinnableModels;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractSkinnableRenderers;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class SkinRendererRegistries {

    public static void init() {
        SkinRendererManager manager = SkinRendererManager.getInstance();
        adapt();
        ModEntityProfiles.addListener(manager::unbind, manager::bind);
        //
        manager.registerPlugin(SlimeSkinRenderer.class, new SlimeOuterFixPlugin<>());
        manager.registerPlugin(VillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(ZombieVillagerSkinRenderer.class, new VillagerLayerFixPlugin<>());
        manager.registerPlugin(BipedSkinRenderer.class, new MobLayerFixPlugin<>());
    }

    protected static void adapt() {
        // using special skin renderer of the arrow.
        registerRenderer(ArrowSkinRenderer::new, null, AbstractSkinnableRenderers.ARROW);
        registerRenderer(TridentSkinRenderer::new, null, AbstractSkinnableRenderers.THROWN_TRIDENT);

        registerRenderer(IllagerSkinRenderer::new, AbstractSkinnableModels.ILLAGER, null);
        registerRenderer(ZombieVillagerSkinRenderer::new, AbstractSkinnableModels.ZOMBIE_VILLAGER, null);

        registerRenderer(VillagerSkinRenderer::new, AbstractSkinnableModels.VILLAGER, null);
        registerRenderer(IronGolemSkinRenderer::new, AbstractSkinnableModels.IRON_GOLE, null);

        registerRenderer(FirstPersonSkinRenderer::new, AbstractSkinnableModels.FIRST_PERSON_PLAYER, null);
        registerRenderer(PlayerSkinRenderer::new, AbstractSkinnableModels.PLAYER, null);
        registerRenderer(BipedSkinRenderer::new, AbstractSkinnableModels.HUMANOID, null);

        registerRenderer(SlimeSkinRenderer::new, AbstractSkinnableModels.SLIME, null);
        registerRenderer(GhastSkinRenderer::new, AbstractSkinnableModels.GHAST, null);
        registerRenderer(ChickenSkinRenderer::new, AbstractSkinnableModels.CHICKEN, null);
        registerRenderer(CreeperSkinRenderer::new, AbstractSkinnableModels.CREEPER, null);

        registerOptionalRenderer(BipedSkinRenderer::new, AbstractSkinnableModels.ALLAY, null);
    }

    protected static void registerRenderer(Function<EntityProfile, SkinRenderer<?, ?>> factory, Class<?> modelClass, Class<?> rendererClass) {
        Builder builder = new Builder();
        builder.factory = factory;
        builder.modelClass = modelClass;
        builder.rendererClass = rendererClass;
        SkinRendererManager.getInstance().registerRenderer(builder);
    }

    protected static void registerOptionalRenderer(Function<EntityProfile, SkinRenderer<?, ?>> factory, Class<?> modelClass, Class<?> rendererClass) {
        if (modelClass != null || rendererClass != null) {
            registerRenderer(factory, modelClass, rendererClass);
        }
    }

    protected static class Builder implements SkinRenderer.Factory<SkinRenderer<?, ?>> {

        Class<?> modelClass;
        Class<?> rendererClass;
        Function<EntityProfile, SkinRenderer<?, ?>> factory;

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
            skinRenderer.initWithRenderer(entityRenderer);
            return skinRenderer;
        }
    }
}
