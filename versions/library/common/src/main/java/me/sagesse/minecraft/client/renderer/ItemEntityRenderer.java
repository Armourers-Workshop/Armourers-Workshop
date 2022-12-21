package me.sagesse.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractItemEntityRenderer;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public abstract class ItemEntityRenderer extends AbstractItemEntityRenderer {

    public abstract void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, IPoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay);

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        renderByItem(itemStack, transformType, MatrixUtils.of(poseStack), renderTypeBuffer, light, overlay);
    }
}
