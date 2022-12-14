package moe.plushie.armourers_workshop.core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.ModContributors;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeLayer<T extends Entity, V extends EntityModel<T>, M extends IModelHolder<V>> extends RenderLayer<T, V> {

    protected final SkinRenderer<T, V, M> skinRenderer;
    protected final RenderLayerParent<T, V> entityRenderer;

    public SkinWardrobeLayer(SkinRenderer<T, V, M> skinRenderer, RenderLayerParent<T, V> renderer) {
        super(renderer);
        this.skinRenderer = skinRenderer;
        this.entityRenderer = renderer;
    }

    @Override
    public void render(PoseStack poseStackIn, MultiBufferSource buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        M model = SkinRendererManager.wrap(getParentModel());
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        if (renderData.overridePostStack != null) {
            poseStack = renderData.overridePostStack;
        }

        poseStack.pushPose();

        // apply the model baby scale.
        applyModelScale(poseStack, model);

        // render the contributor
        ModContributors.Contributor contributor = ModContributors.by(entity);
        if (contributor != null && renderData.shouldRenderExtra()) {
            renderMagicCircle(poseStack, buffers, entity.tickCount + entity.getId() * 31, partialTicks, 24, contributor.color, packedLightIn, OverlayTexture.NO_OVERLAY);
        }

        float f = 1 / 16f;
        poseStack.scale(f, f, f);

        SkinRenderContext context = SkinRenderContext.getInstance();
        float partialTicks2 = TickUtils.ticks();
        for (SkinRenderData.Entry entry : renderData.getArmorSkins()) {
            context.setup(packedLightIn, partialTicks2, null, poseStack, buffers);
            skinRenderer.render(entity, model, entry.getBakedSkin(), entry.getBakedScheme(), entry.getItemStack(), entry.getSlotIndex(), context);
        }
        context.clean();

        poseStack.popPose();
    }

    public void renderMagicCircle(IPoseStack poseStack, MultiBufferSource buffers, int ticks, float partialTickTime, int offset, int color, int lightmap, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0, offset / 16.0f, 0);

        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        float circleScale = 2;
        float rotation = (float) (ticks / 0.8D % 360D) + partialTickTime;
        poseStack.rotate(Vector3f.YP.rotationDegrees(rotation));
        poseStack.scale(circleScale, circleScale, circleScale);
        IMatrix4f mat = poseStack.lastPose();
        VertexConsumer builder = buffers.getBuffer(SkinRenderType.IMAGE_MAGIC);
        builder.vertex(mat, -1, 0, -1).color(red, green, blue, 0xff).uv(1, 0).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, 0xff).uv(0, 0).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(mat, 1, 0, 1).color(red, green, blue, 0xff).uv(0, 1).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(mat, -1, 0, 1).color(red, green, blue, 0xff).uv(1, 1).overlayCoords(overlay).uv2(lightmap).endVertex();
        poseStack.popPose();
    }

    protected void applyModelScale(IPoseStack poseStack, M model) {
        if (model.isBaby()) {
            float scale = 1 / model.getBabyScale();
            IVector3f offset = model.getBabyOffset();
            if (offset == null) {
                return;
            }
            poseStack.scale(scale, scale, scale);
            poseStack.translate(offset.getX() / 16f, offset.getY() / 16f, offset.getZ() / 16f);
        }
    }
}
