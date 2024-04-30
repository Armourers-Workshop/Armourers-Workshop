package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProviderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.21, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractLivingEntityRendererImpl<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> implements AbstractEntityRendererProviderImpl {

    public AbstractLivingEntityRendererImpl(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    public float getEntityScale(T entity) {
        return entity.getScale();
    }

    @Override
    protected void setupRotations(T livingEntity, PoseStack poseStack, float f, float g, float h, float i) {
        poseStack.scale(1 / i, 1 / i, 1 / i);
        super.setupRotations(livingEntity, poseStack, f, g, h, 1);
    }

    @Override
    protected final void scale(T entity, PoseStack poseStack, float f) {
        float newScale = getEntityScale(entity);
        poseStack.scale(newScale, newScale, newScale);
    }
}
