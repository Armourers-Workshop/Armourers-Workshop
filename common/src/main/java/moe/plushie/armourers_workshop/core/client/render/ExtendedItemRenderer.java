package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.Matrix4f;
import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public final class ExtendedItemRenderer {

    public static void renderSkinInBox(SkinDescriptor descriptor, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, IPoseStack poseStack, MultiBufferSource buffers) {
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            renderSkinInBox(bakedSkin, descriptor.getColorScheme(), itemStack, x, y, z, width, height, rx, ry, rz, 0, 0xf000f0, poseStack, buffers);
        }
    }

    public static void renderSkinInBox(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, float partialTicks, int light, IPoseStack poseStack, MultiBufferSource buffers) {
        if (bakedSkin != null) {
            int t = TickUtils.ticks();
            int si = Math.min(width, height);
            poseStack.pushPose();
            poseStack.translate(x + width / 2f, y + height / 2f, z);
            // we need do a vertical mirror, but normal matrix no needs.
            poseStack.lastPose().scale(1, -1, 1);
            poseStack.rotate(Vector3f.XP.rotationDegrees(rx));
            poseStack.rotate(Vector3f.YP.rotationDegrees(ry + (float) (t / 10 % 360)));
            poseStack.scale(0.625f, 0.625f, 0.625f);
            poseStack.scale(si, si, si);
            renderSkinInBox(bakedSkin, scheme, itemStack, null, Vector3f.ONE, 1, 1, 1, partialTicks, light, poseStack, buffers);
            poseStack.popPose();
        }
    }

    public static void renderSkinInBox(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, @Nullable Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, IPoseStack poseStack, MultiBufferSource buffers) {
        Entity entity = SkinItemRenderer.getInstance().getMannequinEntity();
        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null || entity == null || entity.level == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(entity, model, rotation, itemStack);
        float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
        newScale = Math.min(newScale, targetDepth / rect.getDepth());
        RenderSystem.drawTargetBox(poseStack, targetWidth, targetHeight, targetDepth, buffers);

        poseStack.scale(newScale / scale.getX(), newScale / scale.getY(), newScale / scale.getZ());
        poseStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        SkinRenderData renderData = SkinRenderData.of(entity);
        IModelHolder<Model> modelHolder = ModelHolder.of(model);
        SkinRenderContext context = SkinRenderContext.alloc(renderData, light, partialTicks, poseStack, buffers);
        context.setItem(itemStack, 0);
        context.setTransforms(entity, renderer.getOverrideModel(modelHolder));
        renderer.render(entity, modelHolder, bakedSkin, scheme, context);
        context.release();

        poseStack.popPose();
    }

    public static void renderMannequin(PlayerTextureDescriptor descriptor, Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, IPoseStack poseStack, MultiBufferSource buffers) {
        MannequinEntity entity = SkinItemRenderer.getInstance().getMannequinEntity();
        if (entity == null || entity.level == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.rotate(Vector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        Rectangle3f rect = new Rectangle3f(entity.getBoundingBox());
        RenderSystem.drawTargetBox(poseStack, targetWidth, targetHeight, targetDepth, buffers);

        Rectangle3f resolvedRect = rect.offset(rect.getMidX(), rect.getMidY(), rect.getMidZ());
        resolvedRect.mul(new Matrix4f(new Quaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true)));
        float newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        poseStack.scale(newScale, newScale, newScale);
        poseStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, poseStack.cast(), buffers, light));

        poseStack.popPose();
    }
}

