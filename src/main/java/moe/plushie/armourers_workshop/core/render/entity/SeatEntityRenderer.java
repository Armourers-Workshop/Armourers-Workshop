package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SeatEntityRenderer<T extends SeatEntity> extends EntityRenderer<T> {

    public SeatEntityRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        if (AWConfig.debugSkinnableBlock) {
            RenderUtils.drawPoint(matrixStack, renderTypeBuffer);
            RenderUtils.drawBoundingBox(matrixStack, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, Color.ORANGE, renderTypeBuffer);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}