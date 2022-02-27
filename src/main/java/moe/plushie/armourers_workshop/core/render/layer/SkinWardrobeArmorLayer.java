package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.AWContributors;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeArmorLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public SkinWardrobeArmorLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        BipedModel<?> model = getParentModel();
        if (model.young) {
            float scale = 1.0f / model.babyBodyScale;
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0.0f, model.bodyYOffset / 16.0f, 0.0f);
        }

        if (!entity.isInvisible()) {
            AWContributors.Contributor contributor = AWContributors.by(entity);
            if (contributor != null) {
                renderMagicCircle(matrixStack, renderTypeBuffer, entity.tickCount + (int) entity.timeOffs, partialTicks, 24, contributor.color);
            }
        }

        ClientWardrobeHandler.onRenderArmor(entity, model, packedLightIn, matrixStack, renderTypeBuffer);

        matrixStack.popPose();
    }

    public void renderMagicCircle(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticks, float partialTickTime, int offset, int color) {
        matrixStack.pushPose();
        matrixStack.translate(0, offset / 16.0f, 0);

        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        float circleScale = 2;
        float rotation = (float) (ticks / 0.8D % 360D) + partialTickTime;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        matrixStack.scale(circleScale, circleScale, circleScale);
        Matrix4f mat = matrixStack.last().pose();
        IVertexBuilder builder = renderTypeBuffer.getBuffer(SkinRenderType.MAGIC);
        builder.vertex(mat, -1, 0, -1).color(red, green, blue, 0xff).uv(1, 0).endVertex();
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, 0xff).uv(0, 0).endVertex();
        builder.vertex(mat, 1, 0, 1).color(red, green, blue, 0xff).uv(0, 1).endVertex();
        builder.vertex(mat, -1, 0, 1).color(red, green, blue, 0xff).uv(1, 1).endVertex();

        matrixStack.popPose();
    }
}