package moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[18, )")
@Extension
public class ABI {

    public static void mulPose(@This PoseStack poseStack, IQuaternionf q) {
        poseStack.mulPose(AbstractPoseStack.convertQuaternion(q));
    }
}
