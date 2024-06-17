package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class ExtendedItemRenderer {

    public static void renderSkinInGUI(BakedSkin bakedSkin, float x, float y, float z, float width, float height, float rx, float ry, float rz, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, getTarget(bakedSkin), x, y, z, width, height, rx, ry, rz, 0, 0xf000f0, poseStack, bufferSource);
    }

    public static void renderSkinInGUI(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, scheme, itemStack, null, x, y, z, width, height, rx, ry, rz, partialTicks, light, poseStack, bufferSource);
    }

    public static void renderSkinInTooltip(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, scheme, itemStack, Vector3f.ONE, x, y, z, width, height, rx, ry, rz, partialTicks, light, poseStack, bufferSource);
    }

    public static int renderSkinInBox(BakedSkin bakedSkin, ColorScheme scheme, Vector3f scale, float partialTicks, int light, SkinItemSource itemSource, IPoseStack poseStack, IBufferSource bufferSource) {
        return renderSkinInBox(bakedSkin, scheme, scale, getTarget(bakedSkin), partialTicks, light, itemSource, poseStack, bufferSource);
    }

    private static void renderSkinInBox(BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, @Nullable Vector3f target, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        if (bakedSkin != null) {
            float t = TickUtils.animationTicks();
            float si = Math.min(width, height);
            poseStack.pushPose();
            poseStack.translate(x + width / 2f, y + height / 2f, z);
            poseStack.scale(1, -1, 1);
            poseStack.rotate(Vector3f.XP.rotationDegrees(rx));
            poseStack.rotate(Vector3f.YP.rotationDegrees(ry + ((t * 100) % 360)));
            poseStack.scale(0.625f, 0.625f, 0.625f);
            poseStack.scale(si, si, si);
            renderSkinInBox(bakedSkin, scheme, Vector3f.ONE, target, partialTicks, light, SkinItemSource.create(itemStack), poseStack, bufferSource);
            poseStack.popPose();
        }
    }

    private static int renderSkinInBox(BakedSkin bakedSkin, ColorScheme scheme, Vector3f scale, @Nullable Vector3f target, float partialTicks, int light, SkinItemSource itemSource, IPoseStack poseStack, IBufferSource bufferSource) {
        var counter = 0;
        var context = SkinRenderTesselator.create(bakedSkin);
        if (context == null) {
            return counter;
        }
        poseStack.pushPose();
        poseStack.scale(-1, -1, 1);

        context.setLightmap(light);
        context.setPartialTicks(partialTicks);
        context.setRenderData(SkinRenderData.of(context.getMannequin()));
        context.setColorScheme(scheme);
        context.setReferenced(itemSource);

        // ...
        if (target != null) {
            var rect = context.getBakedRenderBounds();
            float targetWidth = target.getX();
            float targetHeight = target.getY();
            float targetDepth = target.getZ();
            float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
            newScale = Math.min(newScale, targetDepth / rect.getDepth());
            if (ModDebugger.targetBounds) {
                ShapeTesselator.stroke(-targetWidth / 2, -targetHeight / 2, -targetDepth / 2, targetWidth / 2, targetHeight / 2, targetDepth / 2, UIColor.ORANGE, poseStack, bufferSource);
                ShapeTesselator.vector(0, 0, 0, targetWidth, targetHeight, targetDepth, poseStack, bufferSource);
            }
            poseStack.scale(newScale / scale.getX(), newScale / scale.getY(), newScale / scale.getZ());
            poseStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center
        } else {
            float newScale = 1 / 16f;
            poseStack.scale(newScale, newScale, newScale);
        }

        counter = context.draw(poseStack, bufferSource);

        poseStack.popPose();

        return counter;
    }

    public static void renderMannequin(PlayerTextureDescriptor descriptor, Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        var entity = PlaceholderManager.MANNEQUIN.get();
        if (entity == null || entity.getLevel() == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.rotate(Vector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        var rect = new Rectangle3f(entity.getBoundingBox());
        if (ModDebugger.targetBounds) {
            ShapeTesselator.stroke(-targetWidth / 2, -targetHeight / 2, -targetDepth / 2, targetWidth / 2, targetHeight / 2, targetDepth / 2, UIColor.ORANGE, poseStack, bufferSource);
            ShapeTesselator.vector(0, 0, 0, targetWidth, targetHeight, targetDepth, poseStack, bufferSource);
        }

        var resolvedRect = rect.offset(rect.getMidX(), rect.getMidY(), rect.getMidZ());
        resolvedRect.mul(new OpenMatrix4f(new OpenQuaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true)));
        var newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        poseStack.scale(newScale, newScale, newScale);
        poseStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        var rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, AbstractPoseStack.unwrap(poseStack), AbstractBufferSource.unwrap(bufferSource), light));

        poseStack.popPose();
    }

    private static Vector3f getTarget(BakedSkin bakedSkin) {
        // when no provided a item model, we will use the default target.
        if (bakedSkin != null && bakedSkin.getItemModel() == null) {
            return Vector3f.ONE;
        }
        return null;
    }
}

