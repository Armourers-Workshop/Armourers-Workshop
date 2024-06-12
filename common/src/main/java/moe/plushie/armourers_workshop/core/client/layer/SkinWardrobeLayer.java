package moe.plushie.armourers_workshop.core.client.layer;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderLayer;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.init.ModContributors;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class SkinWardrobeLayer<T extends Entity, V extends EntityModel<T>, M extends IModel> extends AbstractRenderLayer<T, V> {

    protected final BakedArmature armature;
    protected final RenderLayerParent<T, V> entityRenderer;

    public SkinWardrobeLayer(BakedArmatureTransformer armatureTransformer, RenderLayerParent<T, V> renderer) {
        super(renderer);
        this.armature = new BakedArmature(armatureTransformer.getArmature());
        this.entityRenderer = renderer;
    }

    @Override
    public void render(T entity, float limbSwing, float limbSwingAmount, int packedLightIn, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, IPoseStack poseStack, IBufferSource bufferSource) {
        // respect invisibility potions etc.
        if (entity.isInvisible()) {
            return;
        }
        var poseStack1 = poseStack;
        var renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var renderPatch = renderData.getRenderPatch();
        if (renderPatch == null) {
            return;
        }
        var transformer = renderPatch.getTransformer();
        if (transformer == null) {
            return;
        }
        var epicFlightContext = ObjectUtils.safeCast(renderPatch, EpicFightEntityRendererPatch.class);
        if (epicFlightContext != null) {
            poseStack = epicFlightContext.getOverridePose();
        }

        poseStack.pushPose();

        // apply the model baby scale.
        if (epicFlightContext == null) {
            applyModelScale(poseStack, ModelHolder.of(getParentModel()));
        }

        // render the contributor
        var contributor = ModContributors.by(entity);
        if (contributor != null && renderData.shouldRenderExtra()) {
            renderMagicCircle(poseStack1, bufferSource, entity.tickCount + entity.getId() * 31, partialTicks, 24, contributor.color, packedLightIn, OverlayTexture.NO_OVERLAY);
        }

        float f = 1 / 16f;
        poseStack.scale(f, f, f);

        transformer.applyTo(armature);
        var context = SkinRenderContext.alloc(renderData, packedLightIn, partialTicks, null, poseStack, bufferSource);
        for (var entry : renderData.getArmorSkins()) {
            context.setReferenced(SkinItemSource.create(entry.getRenderPriority(), entry.getItemStack()));
            var bakedSkin = entry.getBakedSkin();
            bakedSkin.setupAnim(entity, context.getAnimationTicks(), context.getReferenced());
            SkinRenderer.render(entity, armature, bakedSkin, entry.getBakedScheme(), context);
        }
        context.release();

        poseStack.popPose();
    }

    public void renderMagicCircle(IPoseStack poseStack, IBufferSource bufferSource, int ticks, float partialTickTime, int offset, int color, int lightmap, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0, offset / 16.0f, 0);

        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        float circleScale = 2;
        float rotation = (float) (ticks / 0.8D % 360D) + partialTickTime;
        poseStack.rotate(Vector3f.YP.rotationDegrees(rotation));
        poseStack.scale(circleScale, circleScale, circleScale);
        var pose = poseStack.last();
        var builder = bufferSource.getBuffer(SkinRenderType.IMAGE_MAGIC);
        builder.vertex(pose, -1, 0, -1).color(red, green, blue, 0xff).uv(1, 0).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(pose, 1, 0, -1).color(red, green, blue, 0xff).uv(0, 0).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(pose, 1, 0, 1).color(red, green, blue, 0xff).uv(0, 1).overlayCoords(overlay).uv2(lightmap).endVertex();
        builder.vertex(pose, -1, 0, 1).color(red, green, blue, 0xff).uv(1, 1).overlayCoords(overlay).uv2(lightmap).endVertex();
        poseStack.popPose();
    }

    protected void applyModelScale(IPoseStack poseStack, M model) {
        IModelBabyPose babyPose = model.getBabyPose();
        if (babyPose != null) {
            float scale = 1 / babyPose.getHeadScale();
            IVector3f offset = babyPose.getHeadOffset();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(offset.getX() / 16f, offset.getY() / 16f, offset.getZ() / 16f);
        }
    }
}
