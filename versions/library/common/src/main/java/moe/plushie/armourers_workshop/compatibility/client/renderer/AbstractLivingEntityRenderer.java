package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererLayerProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends AbstractLivingEntityRendererImpl<T, M> {

    public AbstractLivingEntityRenderer(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public void scale(T livingEntity, PoseStack poseStack, float f) {
        super.scale(livingEntity, poseStack, f);
    }

    @Override
    public void setupRotations(T entity, PoseStack poseStack, float f, float g, float h) {
        super.setupRotations(entity, poseStack, f, g, h);
    }

    public void setModel(M model) {
        this.model = model;
    }

    public AbstractEntityRendererLayerProvider getLayerProvider() {
        return EntityRenderDispatcher.createLayerProvider(this);
    }
}
