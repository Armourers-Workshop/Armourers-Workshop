package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.FloatBuffer;

@Environment(EnvType.CLIENT)
public class PoseUtils {

//    public static IPoseStack wrap(PoseStack poseStack) {
//        return new PoseStackWrapper(poseStack);
//    }
//
//    public static PoseStack unwrap(IPoseStack poseStack) {
//        if (poseStack instanceof PoseStackWrapper) {
//            return ((PoseStackWrapper) poseStack).pose();
//        }
//        PoseStack poseStack1 = new PoseStack();
//        poseStack1.mulPoseMatrix(poseStack.last().pose());
//        poseStack1.mulNormalMatrix(poseStack.last().normal());
//        return poseStack1;
//    }

    public static OpenMatrix4f createPoseMatrix(FloatBuffer buffer) {
        return new OpenMatrix4f(buffer);
    }

    public static OpenMatrix3f createNormalMatrix(FloatBuffer buffer) {
        return new OpenMatrix3f(buffer);
    }
}
