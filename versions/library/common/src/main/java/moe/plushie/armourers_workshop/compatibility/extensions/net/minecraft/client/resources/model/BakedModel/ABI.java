package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.resources.model.BakedModel;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
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

    public static void applyTransform(@This BakedModel bakedModel, IPoseStack poseStack, boolean leftHandHackery, AbstractItemTransformType transformType) {
        PoseStack poseStack1 = AbstractPoseStack.unwrap(poseStack);
        TransformationProvider.handleTransforms(poseStack1, bakedModel, transformType, leftHandHackery);
        IPoseStack resultStack = AbstractPoseStack.wrap(poseStack1);
        // fallback when pose stack is non-vanilla stack.
        if (resultStack != poseStack) {
            poseStack.last().set(resultStack.last());
        }
    }
}
