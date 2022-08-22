package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public final class ExtendedItemRenderer {

    public static void renderSkin(SkinDescriptor descriptor, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, PoseStack matrixStack, MultiBufferSource buffers) {
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            renderSkin(bakedSkin, descriptor.getColorScheme(), itemStack, x, y, z, width, height, rx, ry, rz, matrixStack, buffers);
        }
    }

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, int x, int y, int z, int width, int height, int rx, int ry, int rz, PoseStack matrixStack, MultiBufferSource buffers) {
        if (bakedSkin != null) {
            int t = TickUtils.ticks();
            int si = Math.min(width, height);
            matrixStack.pushPose();
            matrixStack.translate(x + width / 2f, y + height / 2f, z);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(rx));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(ry - (float) (t / 10 % 360)));
            matrixStack.scale(0.625f, 0.625f, 0.625f);
            matrixStack.scale(si, si, si);
            matrixStack.scale(-1, 1, 1);
            renderSkin(bakedSkin, scheme, itemStack, null, Vector3f.ONE, 1, 1, 1, 0, 0xf000f0, matrixStack, buffers);
            matrixStack.popPose();
        }
    }

    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, @Nullable Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers) {
        Entity entity = SkinItemRenderer.getInstance().getMannequinEntity();
        HumanoidModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null || entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(entity, model, rotation, itemStack);
        float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
        newScale = Math.min(newScale, targetDepth / rect.getDepth());
        RenderSystem.drawTargetBox(matrixStack, targetWidth, targetHeight, targetDepth, buffers);

        matrixStack.scale(newScale / scale.getX(), newScale / scale.getY(), newScale / scale.getZ());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, partialTicks, matrixStack, buffers);
        renderer.render(entity, model, bakedSkin, scheme, itemStack, 0, context);

        matrixStack.popPose();
    }

    public static void renderMannequin(PlayerTextureDescriptor descriptor, Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers) {
        MannequinEntity entity = SkinItemRenderer.getInstance().getMannequinEntity();
        if (entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        Rectangle3f rect = new Rectangle3f(entity.getBoundingBox());
        RenderSystem.drawTargetBox(matrixStack, targetWidth, targetHeight, targetDepth, buffers);

        Rectangle3f resolvedRect = rect.offset(rect.getMidX(), rect.getMidY(), rect.getMidZ());
        resolvedRect.mul(new Matrix4f(TrigUtils.rotate(rotation.getX(), rotation.getY(), rotation.getZ(), true)));
        float newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        matrixStack.scale(newScale, newScale, newScale);
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, matrixStack, buffers, light));

        matrixStack.popPose();
    }
}

