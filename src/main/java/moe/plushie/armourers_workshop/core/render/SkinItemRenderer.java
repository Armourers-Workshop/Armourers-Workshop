package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.entity.SkinDummyEntity;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    public static int mp1 = 0;
    private static ColorScheme entityDye = ColorScheme.EMPTY;

    public static void renderSkin(BakedSkin bakedSkin, int light, int partialTicks, int targetWidth, int targetHeight, ItemCameraTransforms.TransformType transformType, Vector3f rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        SkinDummyEntity entity = SkinDummyEntity.shared();
        Rectangle3f rect = bakedSkin.getRenderBounds(entity, entity.getModel(), rotation);

        float width = rect.getWidth();
        float height = rect.getHeight();

        // with non gui render, we need make sure has enough area.
        if (transformType != ItemCameraTransforms.TransformType.GUI && transformType != ItemCameraTransforms.TransformType.FIXED) {
            double d = Math.sqrt(rect.getWidth() * rect.getWidth() + rect.getHeight() * rect.getHeight() + rect.getDepth() * rect.getDepth());
            width = (float) d;
            height = (float) d;
        }
        matrixStack.pushPose();

        float scale = Math.min(targetWidth / width, targetHeight / height);
        matrixStack.scale(-scale, -scale, scale);
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ());

        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
        SkinModelRenderer.renderSkin(bakedSkin, entityDye, entity, entity.getModel(), ItemCameraTransforms.TransformType.NONE, light, partialTicks, matrixStack, buffer1);
        buffer1.endBatch();

        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({"deprecation", "NullableProblems"})
    public static class ItemStackRenderer extends ItemStackTileEntityRenderer {

        @Override
        public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
            BakedSkin skin = SkinBakery.getInstance().loadSkin(SkinDescriptor.of(itemStack));
            if (skin == null) {
                return;
            }
            IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
            ItemTransformVec3f transform = bakedModel.getTransforms().getTransform(transformType);

            matrixStack.pushPose();
            matrixStack.translate(0.5F, 0.5F, 0.5F); // reset to center

            SkinItemRenderer.renderSkin(skin, light, 0, 1, 1, transformType, transform.rotation, matrixStack, renderTypeBuffer);

            matrixStack.popPose();
        }
    }
}