package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.init.common.ModContributors;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinWardrobeLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    public SkinWardrobeLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        matrixStack.pushPose();

        EntityModel<?> entityModel = getParentModel();
        ClientWardrobeHandler.onRenderArmorPre(entity, entityModel, packedLightIn, matrixStack, buffers);

        // render the contributor
        ModContributors.Contributor contributor = ModContributors.by(entity);
        if (contributor != null) {
            renderMagicCircle(matrixStack, buffers, entity.tickCount + entity.getId() * 31, partialTicks, 24, contributor.color);
        }

        ClientWardrobeHandler.onRenderArmor(entity, entityModel, packedLightIn, matrixStack, buffers);

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