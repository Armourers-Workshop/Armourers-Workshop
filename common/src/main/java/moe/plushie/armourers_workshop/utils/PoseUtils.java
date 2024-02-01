package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.FloatBuffer;

@Environment(EnvType.CLIENT)
public class PoseUtils {

    public static PoseStackWrapper wrap(PoseStack poseStack) {
        return new PoseStackWrapper(poseStack);
    }

    public static PoseStack unwrap(IPoseStack poseStack) {
        if (poseStack instanceof PoseStackWrapper) {
            return ((PoseStackWrapper) poseStack).pose();
        }
        PoseStack poseStack1 = new PoseStack();
        poseStack1.mulPoseMatrix(poseStack.lastPose());
        poseStack1.mulNormalMatrix(poseStack.lastNormal());
        return poseStack1;
    }

    public static OpenMatrix4f createPoseMatrix(FloatBuffer buffer) {
        return new OpenMatrix4f(buffer);
    }

    public static OpenMatrix3f createNormalMatrix(FloatBuffer buffer) {
        return new OpenMatrix3f(buffer);
    }
}
