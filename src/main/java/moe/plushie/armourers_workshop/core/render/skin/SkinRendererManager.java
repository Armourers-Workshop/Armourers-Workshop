package moe.plushie.armourers_workshop.core.render.skin;

import com.google.common.collect.Maps;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

    public <T extends Entity, M extends Model> void register(EntityProfile<T> entityProfile, Function<EntityType<T>, SkinRenderer<?, ?>> rendererBuilder) {
        EntityType<T> entityType = entityProfile.getEntityType();
        EntityRendererManager entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        // Add our own custom armor layer to the various player renderers.
        if (entityType == EntityType.PLAYER) {
            for (PlayerRenderer playerRenderer : entityRenderManager.getSkinMap().values()) {
                addSkinLayer(EntityType.PLAYER, playerRenderer);
            }
        }
        // Add our own custom armor layer to everything that has an armor layer
        entityRenderManager.renderers.forEach((entityType1, entityRenderer) -> {
            if (!entityType.equals(entityType1)) {
                return;
            }
            if (entityRenderer instanceof LivingRenderer<?, ?>) {
                LivingRenderer<?, ?> livingRenderer = (LivingRenderer<?, ?>) entityRenderer;
                addSkinLayer(entityType1, livingRenderer);
            }
        });
        this.renderers.put(entityType, rendererBuilder.apply(entityType));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Entity, M extends Model> SkinRenderer<T, M> getRenderer(@Nullable T entity) {
        if (entity == null) {
            return null;
        }
        return (SkinRenderer<T, M>) this.renderers.get(entity.getType());
    }

    private <T extends LivingEntity, M extends EntityModel<T>> void addSkinLayer(EntityType<?> type, LivingRenderer<T, M> renderer) {
        renderer.addLayer(new SkinWardrobeArmorLayer<>(renderer));
    }
}
