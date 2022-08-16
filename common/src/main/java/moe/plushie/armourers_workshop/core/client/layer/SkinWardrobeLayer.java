package moe.plushie.armourers_workshop.core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.init.ModContributors;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    protected final SkinRenderer<T, M> skinRenderer;
    protected final RenderLayerParent<T, M> entityRenderer;

    public SkinWardrobeLayer(SkinRenderer<T, M> skinRenderer, RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.skinRenderer = skinRenderer;
        this.entityRenderer = renderer;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        M model = getParentModel();
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();

        // apply the model baby scale.
        applyModelScale(matrixStack, model);

        // render the contributor
        ModContributors.Contributor contributor = ModContributors.by(entity);
        if (contributor != null && renderData.shouldRenderExtra()) {
            renderMagicCircle(matrixStack, buffers, entity.tickCount + entity.getId() * 31, partialTicks, 24, contributor.color);
        }

        float f = 1 / 16f;
        matrixStack.scale(f, f, f);

        SkinRenderContext context = SkinRenderContext.getInstance();
        float partialTicks2 = TickUtils.ticks();
        for (SkinRenderData.Entry entry : renderData.getArmorSkins()) {
            context.setup(packedLightIn, partialTicks2, null, matrixStack, buffers);
            skinRenderer.render(entity, model, entry.getBakedSkin(), entry.getBakedScheme(), entry.getItemStack(), entry.getSlotIndex(), context);
        }

        matrixStack.popPose();
    }

    public void renderMagicCircle(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int ticks, float partialTickTime, int offset, int color) {
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
        VertexConsumer builder = renderTypeBuffer.getBuffer(SkinRenderType.MAGIC);
        builder.vertex(mat, -1, 0, -1).color(red, green, blue, 0xff).uv(1, 0).endVertex();
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, 0xff).uv(0, 0).endVertex();
        builder.vertex(mat, 1, 0, 1).color(red, green, blue, 0xff).uv(0, 1).endVertex();
        builder.vertex(mat, -1, 0, 1).color(red, green, blue, 0xff).uv(1, 1).endVertex();

        matrixStack.popPose();
    }

    protected void applyModelScale(PoseStack matrixStack, M model) {
        if (model.young && model instanceof HumanoidModel) {
            HumanoidModel<?> bipedModel = (HumanoidModel<?>) model;
            float scale = 1.0f / bipedModel.babyBodyScale;
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0.0f, bipedModel.bodyYOffset / 16.0f, 0.0f);
        }
    }
}
