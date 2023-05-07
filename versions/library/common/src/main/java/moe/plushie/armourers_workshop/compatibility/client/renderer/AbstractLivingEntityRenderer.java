package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererLayerProvider;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends AbstractLivingEntityRendererImpl<T, M> {

    public AbstractLivingEntityRenderer(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    public void render(T entity, float f, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        super.render(entity, f, packedLightIn, poseStack.cast(), buffers, packedLightIn);
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        this.render(entity, f, g, MatrixUtils.of(poseStack), multiBufferSource, i);
    }

    public void scale(T entity, IPoseStack poseStack, float f) {
        super.scale(entity, poseStack.cast(), f);
    }

    @Override
    protected void scale(T livingEntity, PoseStack poseStack, float f) {
        this.scale(livingEntity, MatrixUtils.of(poseStack), f);
    }

    public void setupRotations(T entity, IPoseStack poseStack, float f, float g, float h) {
        super.setupRotations(entity, poseStack.cast(), f, g, h);
    }

    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float f, float g, float h) {
        this.setupRotations(entity, MatrixUtils.of(poseStack), f, g, h);
    }

    public void setModel(M model) {
        this.model = model;
    }

    public AbstractEntityRendererLayerProvider getLayerProvider() {
        return EntityRenderDispatcher.createLayerProvider(this);
    }
}
