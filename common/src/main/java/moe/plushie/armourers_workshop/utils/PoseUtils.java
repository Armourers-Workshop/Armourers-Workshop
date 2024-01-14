package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
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

    public static void apply(PoseStack poseStack, ITransformf transform) {
        if (transform.isIdentity()) {
            return;
        }
        IVector3f translate = transform.getTranslate();
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        IVector3f rotation = transform.getRotation();
        if (rotation != Vector3f.ZERO) {
            IVector3f pivot = transform.getPivot();
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        IVector3f scale = transform.getScale();
        if (scale != Vector3f.ONE) {
            poseStack.mulPoseMatrix(OpenMatrix4f.createScaleMatrix(scale.getX(), scale.getY(), scale.getZ()));
            poseStack.mulNormalMatrix(OpenMatrix3f.createScaleMatrix(scale.getX(), scale.getY(), scale.getZ()));
        }
        IVector3f offset = transform.getOffset();
        if (offset != Vector3f.ZERO) {
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public static OpenMatrix4f createPoseMatrix(FloatBuffer buffer) {
        return new OpenMatrix4f(buffer);
    }

    public static OpenMatrix3f createNormalMatrix(FloatBuffer buffer) {
        return new OpenMatrix3f(buffer);
    }
}
