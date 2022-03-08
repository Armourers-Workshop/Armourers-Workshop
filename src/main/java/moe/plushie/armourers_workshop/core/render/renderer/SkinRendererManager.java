package moe.plushie.armourers_workshop.core.render.renderer;

import com.google.common.collect.Maps;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SkinRendererManager {

    private static final SkinRendererManager INSTANCE = new SkinRendererManager();
    private final Map<EntityType<?>, SkinRenderer<?, ?>> renderers = Maps.newHashMap();

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
        if (entity != null) {
            return (SkinRenderer<T, M>) this.renderers.get(entity.getType());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity, M extends Model> void setupRenderer(EntityType<?> entityType, EntityRenderer<?> entityRenderer, SkinRenderer<T, M> skinRenderer) {
        skinRenderer.init((EntityRenderer<T>) entityRenderer);
    }
}
