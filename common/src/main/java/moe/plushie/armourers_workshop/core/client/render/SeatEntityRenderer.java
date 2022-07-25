package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

@Environment(value = EnvType.CLIENT)
public class SeatEntityRenderer<T extends SeatEntity> extends EntityRenderer<T> {

    public SeatEntityRenderer(EntityRenderDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int packedLightIn) {
        if (ModDebugger.skinnableBlock) {
            RenderUtils.drawPoint(matrixStack, buffers);
            RenderUtils.drawBoundingBox(matrixStack, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, Color.ORANGE, buffers);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}