package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractModelViewStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class ExtendedItemRenderer {

    public static void renderSkinInGUI(BakedSkin bakedSkin, float x, float y, float z, float width, float height, float rx, float ry, float rz, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, SkinPaintScheme.EMPTY, ItemStack.EMPTY, OpenVector3f.ONE, x, y, z, width, height, rx, ry, rz, 0, 0xf000f0, 0, poseStack, bufferSource);
    }

    public static void renderSkinInGUI(BakedSkin bakedSkin, SkinPaintScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, scheme, itemStack, OpenVector3f.ONE, x, y, z, width, height, rx, ry, rz, partialTicks, light, 0, poseStack, bufferSource);
    }

    public static void renderSkinInTooltip(BakedSkin bakedSkin, SkinPaintScheme scheme, ItemStack itemStack, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        renderSkinInBox(bakedSkin, scheme, itemStack, OpenVector3f.ONE, x, y, z, width, height, rx, ry, rz, partialTicks, light, 0, poseStack, bufferSource);
    }

    public static int renderSkinInBox(BakedSkin bakedSkin, SkinPaintScheme scheme, float partialTicks, int light, SkinItemSource itemSource, IPoseStack poseStack, IBufferSource bufferSource) {
        return renderSkinInBox(bakedSkin, scheme, OpenVector3f.ONE, partialTicks, light, 0, itemSource, poseStack, bufferSource);
    }

    public static int renderSkinInBox(BakedSkin bakedSkin, SkinPaintScheme scheme, float partialTicks, int light, int outlineColor, SkinItemSource itemSource, IPoseStack poseStack, IBufferSource bufferSource) {
        return renderSkinInBox(bakedSkin, scheme, OpenVector3f.ONE, partialTicks, light, outlineColor, itemSource, poseStack, bufferSource);
    }

    private static void renderSkinInBox(BakedSkin bakedSkin, SkinPaintScheme scheme, ItemStack itemStack, @Nullable OpenVector3f targetBox, float x, float y, float z, float width, float height, float rx, float ry, float rz, float partialTicks, int light, int outlineColor, IPoseStack poseStack, IBufferSource bufferSource) {
        if (bakedSkin == null) {
            return;
        }
        var t = TickUtils.animationTicks();
        var si = Math.min(width, height);
        poseStack.pushPose();
        poseStack.translate(x + width / 2f, y + height / 2f, z);
        poseStack.scale(1, -1, 1);
        poseStack.rotate(OpenVector3f.XP.rotationDegrees(rx));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(ry + (float) ((t * 100) % 360)));
        poseStack.scale(0.625f, 0.625f, 0.625f);
        poseStack.scale(si, si, si);
        renderSkinInBox(bakedSkin, scheme, targetBox, partialTicks, light, outlineColor, SkinItemSource.create(itemStack), poseStack, bufferSource);
        poseStack.popPose();
    }

    private static int renderSkinInBox(BakedSkin bakedSkin, SkinPaintScheme scheme, @Nullable OpenVector3f targetBox, float partialTicks, int light, int outlineColor, SkinItemSource itemSource, IPoseStack poseStack, IBufferSource bufferSource) {
        var counter = 0;
        var tesselator = SkinRenderTesselator.create(bakedSkin);
        if (tesselator == null) {
            return counter;
        }
        poseStack.pushPose();
        poseStack.scale(-1, -1, 1);

        tesselator.setLightmap(light);
        tesselator.setPartialTicks(partialTicks);

        tesselator.setRenderData(EntityRenderData.of(tesselator.mannequin()));
        tesselator.setColorScheme(scheme);
        tesselator.setItemSource(itemSource);
        tesselator.setUseItemTransforms(true);
        tesselator.setOutlineColor(outlineColor);
        tesselator.setDisplayBox(targetBox);
        tesselator.setDisplayContext(itemSource.displayContext());

        float f = 1 / 16f;
        poseStack.scale(f, f, f);

        tesselator.setPoseStack(poseStack);
        tesselator.setBufferSource(bufferSource);
        tesselator.setModelViewStack(AbstractModelViewStack.getInstance());

        counter = tesselator.draw();

        poseStack.popPose();

        return counter;
    }

    public static void renderMannequin(EntityTextureDescriptor descriptor, OpenVector3f rotation, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, IPoseStack poseStack, IBufferSource bufferSource) {
        var entity = PlaceholderManager.MANNEQUIN.get();
        if (entity == null || entity.getLevel() == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        var aabb = entity.getBoundingBox();
        var rect = new OpenRectangle3f(aabb.minX, aabb.minY, aabb.minZ, aabb.getXsize(), aabb.getYsize(), aabb.getZsize());
        if (ModDebugger.targetBounds) {
            ShapeTesselator.stroke(-targetWidth / 2, -targetHeight / 2, -targetDepth / 2, targetWidth / 2, targetHeight / 2, targetDepth / 2, UIColor.ORANGE, poseStack, bufferSource);
            ShapeTesselator.vector(0, 0, 0, targetWidth, targetHeight, targetDepth, poseStack, bufferSource);
        }

        var resolvedRect = rect.offset(rect.midX(), rect.midY(), rect.midZ());
        resolvedRect.mul(new OpenMatrix4f(new OpenQuaternionf(rotation.x(), rotation.y(), rotation.z(), true)));
        var newScale = Math.min(targetWidth / resolvedRect.width(), targetHeight / resolvedRect.height());

        poseStack.scale(newScale, newScale, newScale);
        poseStack.translate(-rect.midX(), -rect.midY(), -rect.midZ()); // to model center

        var rendererManager = EnvironmentManager.getClient().getEntityRenderDispatcher();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, AbstractPoseStack.unwrap(poseStack), AbstractBufferSource.unwrap(bufferSource), light));

        poseStack.popPose();
    }

//    private static Vector3f getTargetBox(BakedSkin bakedSkin) {
//        // when no provided a item model, we will use the default target.
//        if (bakedSkin != null && bakedSkin.getItemModel() == null) {
//            return Vector3f.ONE;
//        }
//        return null;
//    }
}

