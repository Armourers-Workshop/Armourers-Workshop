package me.sagesse.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractLivingEntityRenderer;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends AbstractLivingEntityRenderer<T, M> {

    public LivingEntityRenderer(AbstractEntityRendererContext context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    public void render(T entity, float p_225623_2_, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        super.render(entity, p_225623_2_, partialTicks, poseStack.cast(), buffers, packedLightIn);
    }

    protected void scale(T livingEntity, IPoseStack poseStack, float f) {
        super.scale(livingEntity, poseStack.cast(), f);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        render(entity, p_225623_2_, partialTicks, MatrixUtils.of(poseStack), buffers, packedLightIn);
    }

    protected void scale(T livingEntity, PoseStack poseStack, float f) {
        scale(livingEntity, MatrixUtils.of(poseStack), f);
    }
}
