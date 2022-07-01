package moe.plushie.armourers_workshop.core.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    public static void renderSkin(SkinDescriptor descriptor, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            renderSkin(bakedSkin, descriptor.getColorScheme(), itemStack, x, y, z, width, height, rx, ry, rz, matrixStack, buffers);
        }
    }

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        if (bakedSkin != null) {
            int t = (int) System.currentTimeMillis();
            int si = Math.min(width, height);
            matrixStack.pushPose();
            matrixStack.translate(x + width / 2f, y + height / 2f, z);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(rx));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(ry - (float) (t / 10 % 360)));
            matrixStack.scale(0.625f, 0.625f, 0.625f);
            matrixStack.scale(si, si, si);
            matrixStack.scale(-1, 1, 1);
            renderSkin(bakedSkin, scheme, itemStack, null, AWConstants.ONE, 1, 1, 1, 0, 0xf000f0, matrixStack, buffers);
            matrixStack.popPose();
        }
    }

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, @Nullable Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Entity entity = SkinItemStackRenderer.getInstance().getMannequinEntity();
        BipedModel<?> model = SkinItemStackRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null || entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(entity, model, rotation, itemStack);
        float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
        newScale = Math.min(newScale, targetDepth / rect.getDepth());
        RenderUtils.drawTargetBox(matrixStack, targetWidth, targetHeight, targetDepth, buffers);

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        renderer.render(entity, model, bakedSkin, scheme, itemStack, ItemCameraTransforms.TransformType.NONE, light, partialTicks, 0, matrixStack, buffers);

        matrixStack.popPose();
    }

    public static void renderMannequin(PlayerTextureDescriptor descriptor, Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        MannequinEntity entity = SkinItemStackRenderer.getInstance().getMannequinEntity();
        if (entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        Rectangle3f rect = new Rectangle3f(entity.getBoundingBox());
        RenderUtils.drawTargetBox(matrixStack, targetWidth, targetHeight, targetDepth, buffers);

        Rectangle3f resolvedRect = rect.offset(rect.getMidX(), rect.getMidY(), rect.getMidZ());
        resolvedRect.mul(new Matrix4f(TrigUtils.rotate(rotation.x(), rotation.y(), rotation.z(), true)));
        float newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, matrixStack, buffers, light));

        matrixStack.popPose();
    }
}

