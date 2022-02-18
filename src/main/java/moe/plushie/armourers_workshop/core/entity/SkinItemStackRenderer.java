package moe.plushie.armourers_workshop.core.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class SkinItemStackRenderer extends ItemStackTileEntityRenderer {

    public static SkinItemStackRenderer INSTANCE = new SkinItemStackRenderer();

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
        IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        ItemTransformVec3f transform = bakedModel.getTransforms().getTransform(transformType);
        BakedSkin skin = AWCore.bakery.loadSkin(SkinDescriptor.of(itemStack));
        if (skin == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5F, 0.5F, 0.5F); // reset to center

        SkinItemRenderer.renderSkin(skin, light, 0, 1, 1, transformType, transform.rotation, matrixStack, renderTypeBuffer);

        matrixStack.popPose();
    }
}
