package me.sagesse.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

@Environment(value = EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> extends AbstractEntityRenderer<T> {

    protected EntityRenderer(AbstractEntityRendererContext context) {
        super(context);
    }

    public void render(T entity, float p_225623_2_, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        super.render(entity, p_225623_2_, partialTicks, poseStack.cast(), buffers, packedLightIn);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        render(entity, p_225623_2_, partialTicks, MatrixUtils.of(poseStack), buffers, packedLightIn);
    }
}
