package moe.plushie.armourers_workshop.core.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        renderSkin(bakedSkin, scheme, null, AWConstants.ONE, 1, 1, 1, partialTicks, light, matrixStack, buffers);
    }

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, @Nullable Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Entity entity = SkinItemStackRenderer.getInstance().getMannequinEntity();
        BipedModel<?> model = SkinItemStackRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity);
        if (renderer == null || entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(entity, model, rotation);
        float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
        newScale = Math.min(newScale, targetDepth / rect.getDepth());
        RenderUtils.drawTargetBox(matrixStack, targetWidth, targetHeight, targetDepth, buffers);

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        renderer.render(entity, model, bakedSkin, scheme, ItemCameraTransforms.TransformType.NONE, light, partialTicks, matrixStack, buffers);

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
        resolvedRect.mul(new Matrix4f(new Quaternion(rotation.x(), rotation.y(), rotation.z(), true)));
        float newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, matrixStack, buffers, light));

        matrixStack.popPose();
    }

}

