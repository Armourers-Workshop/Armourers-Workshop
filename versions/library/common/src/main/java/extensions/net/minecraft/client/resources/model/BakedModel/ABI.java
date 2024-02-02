package extensions.net.minecraft.client.resources.model.BakedModel;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.init.platform.TransformationProvider;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class ABI {

    public static ItemTransform getTransform(@This BakedModel bakedModel, AbstractItemTransformType transformType) {
        return bakedModel.getTransforms().getTransform(ItemTransforms.ofType(transformType));
    }

    public static void applyTransform(@This BakedModel bakedModel, PoseStack poseStack, boolean leftHandHackery, AbstractItemTransformType transformType) {
        TransformationProvider.handleTransforms(poseStack, bakedModel, transformType, leftHandHackery);
    }
}
