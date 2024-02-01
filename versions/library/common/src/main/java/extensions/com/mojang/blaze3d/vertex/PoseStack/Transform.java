package extensions.com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.PoseStackWrapper;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class Transform {

    private static final PoseStackWrapper CONVERTER = new PoseStackWrapper(null);

    public static void applyTransform(@This PoseStack poseStack, ITransformf transform) {
        if (!transform.isIdentity()) {
            CONVERTER.set(poseStack);
            transform.apply(CONVERTER);
        }
    }

    public static void applyTransform(@This PoseStack poseStack, IJointTransform transform) {
        if (transform != IJointTransform.NONE) {
            CONVERTER.set(poseStack);
            transform.apply(CONVERTER);
        }
    }
}
